package org.swdc.note.core.files.storages;

import jakarta.inject.Inject;
import javafx.stage.FileChooser;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.filters.FluentFilter;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.dizitart.no2.repository.ObjectRepository;
import org.dizitart.no2.repository.annotations.Id;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.dependency.annotations.Prototype;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.files.ExternalStorage;
import org.swdc.note.core.nitire.JacksonMapperModule;
import org.swdc.note.core.service.ContentService;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

class ExArticleType {

    private String typeId;

    private String parentId;

    private String name;

    private List<ExArticleType> children = new ArrayList<>();

    private List<ExArticle> articles;

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ExArticleType> getChildren() {
        return children;
    }

    public void setChildren(List<ExArticleType> children) {
        this.children = children;
    }

    public List<ExArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<ExArticle> articles) {
        this.articles = articles;
    }
}

class ExArticle {
    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Id
    private String articleId;
    private String typeId;
    private String title;



}

@Prototype
@MultipleImplement(ExternalStorage.class)
public class NoSqlExtStorage implements ExternalStorage {

    private Nitrite nitrite;

    private ObjectRepository<ExArticleType> typeRepository;
    private ObjectRepository<ExArticle> articleRepository;
    private ObjectRepository<ArticleContent> contentRepository;

    @Inject
    private ContentService contentService;

    @Override
    public boolean open(File file) {
        try {

            MVStoreModule module = MVStoreModule.withConfig()
                    .filePath(file)
                    .build();

            this.nitrite = Nitrite.builder()
                    .loadModule(module)
                    .loadModule(new JacksonMapperModule())
                    .openOrCreate();

            typeRepository = nitrite.getRepository(ExArticleType.class);
            articleRepository = nitrite.getRepository(ExArticle.class);
            contentRepository = nitrite.getRepository(ArticleContent.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void close() {
        nitrite.commit();
        nitrite.close();
    }

    @Override
    public List<ArticleType> loadContents() {
        return typeRepository
                .find(FluentFilter.where("parentId").eq(null))
                .toList()
                .stream()
                .map(type -> this.mapExtType(type,null))
                .collect(Collectors.toList());
    }

    private String addTypeInternal(ArticleType type, String parentId) {

        if (type.getChildren().size() == 0 && type.getArticles().size() == 0) {
            return null;
        }

       ExArticleType stored = getTypeInternal(type.getId(),parentId, type.getName());

        if (stored == null) {
            stored = new ExArticleType();
            stored.setName(type.getName());
            stored.setTypeId(type.getId());
            stored.setParentId(parentId);
            typeRepository.insert(stored);
        }

        Set<Article> articles = type.getArticles();

        for (Article article: articles) {
            addArticleInternal(article,type.getId());
        }

        List<ArticleType> subTypes = type.getChildren();
        for (ArticleType item: subTypes) {
            addTypeInternal(item, stored.getTypeId());
        }

        return stored.getTypeId();
    }

    private String addArticleInternal(Article article, String typeId) {

        ;
        ExArticle stored = articleRepository.find(
                FluentFilter.where("articleId")
                        .eq(article.getId())
                        .and(FluentFilter.where(typeId).eq(typeId))
                ).firstOrNull();

        if (stored == null) {
            stored = new ExArticle();
            stored.setArticleId(article.getId());
            stored.setTitle(article.getTitle());
            stored.setTypeId(typeId);
            articleRepository.insert(stored);
            ArticleContent content = contentService.getArticleContent(article.getId());
            contentRepository.insert(content);
            return stored.getArticleId();
        }
        stored.setTitle(article.getTitle());
        articleRepository.update(stored);
        ArticleContent content = contentService.getArticleContent(article.getId());
        contentRepository.update(content);
        return stored.getArticleId();
    }

    private ExArticleType getTypeInternal(String id,String parentId, String name) {

        return typeRepository.find(FluentFilter.where("typeId").eq(id).and(
                        FluentFilter.where("name").eq(name).and(
                                FluentFilter.where("parentId").eq(parentId)
                        )
                )).firstOrNull();
    }

    @Override
    public String addType(ArticleType type) {
        ArticleType parent = type.getParent();
        ExArticleType exParent = null;
        while (parent != null) {
            ExArticleType parentNext = getTypeInternal(parent.getId(),
                    parent.getParent() != null ? parent.getParent().getId() : null,
                    parent.getName());
            if (parentNext == null) {
                break;
            } else {
                exParent = parentNext;
                parent = parent.getParent();
            }
        }
        return addTypeInternal(type,exParent == null ? null : exParent.getTypeId());
    }

    @Override
    public String addArticle(Article article) {
        ArticleType type = article.getType();
        ExArticleType exist = getTypeInternal(type.getId(),type.getParent() != null ? type.getParent().getId():null,type.getName());
        if (exist != null) {
            return addArticleInternal(article,exist.getTypeId());
        } else {
            String typeId = addType(type);
            return addArticleInternal(article,typeId);
        }
    }

    @Override
    public void deleteArticle(Article article) {

    }

    @Override
    public void deleteType(ArticleType type) {

    }

    /**
     * 目的是转换ExType到ArticleType。
     * ArticleType是外面的标准类型，ExType仅仅是适应存储需要。
     * @param type
     * @param parent
     * @return
     */
    private ArticleType mapExtType(ExArticleType type,ArticleType parent) {
        ArticleType realType = new ArticleType();
        realType.setParent(parent);
        realType.setName(type.getName());
        realType.setId(type.getTypeId());

        List<ArticleType> child = new ArrayList<>();

        // 枚举子分类，并且进行处理

        List<ExArticleType> childs = typeRepository
                .find(FluentFilter.where("parentId").eq(type.getTypeId()))
                .toList();

        for (ExArticleType articleType: childs){
            child.add(mapExtType(articleType,realType));
        }

        List<ExArticle> articles = articleRepository
                .find(FluentFilter.where("typeId").eq(type.getTypeId())).toList();

        Set<Article> articleSet = new HashSet<>();

        for (ExArticle article: articles) {
            Article target = new Article();
            target.setId(article.getArticleId());
            target.setTitle(article.getTitle());
            target.setType(realType);
            articleSet.add(target);
        }

        realType.setChildren(child);
        realType.setArticles(articleSet);
        return realType;
    }

    @Override
    public ArticleContent getContent(String articleId) {

        return contentRepository
                .find(FluentFilter.where("articleId").eq(articleId))
                .firstOrNull();
    }

    @Override
    public FileChooser.ExtensionFilter getFilter() {
        return new FileChooser.ExtensionFilter("数据集","*.noteset");
    }

    @Override
    public String getFileTypeName() {
        return "数据集";
    }

}
