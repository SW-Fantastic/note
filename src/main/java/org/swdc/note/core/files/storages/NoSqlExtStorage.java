package org.swdc.note.core.files.storages;

import javafx.stage.FileChooser;
import lombok.Data;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.swdc.fx.anno.Scope;
import org.swdc.fx.anno.ScopeType;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.service.ContentService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
class ExArticleType {

    private String typeId;

    private String parentId;

    private String name;

    private List<ExArticleType> children = new ArrayList<>();

    private List<ExArticle> articles;

}

@Data
class ExArticle {

    @Id
    private String articleId;
    private String typeId;
    private String title;

}

@Scope(ScopeType.MULTI)
public class NoSqlExtStorage extends AbstractArticleStorage {

    private Nitrite nitrite;

    private ObjectRepository<ExArticleType> typeRepository;
    private ObjectRepository<ExArticle> articleRepository;
    private ObjectRepository<ArticleContent> contentRepository;

    @Override
    public void open(File file) {
        this.nitrite = Nitrite.builder()
                .filePath(file)
                .openOrCreate();

        typeRepository = nitrite.getRepository(ExArticleType.class);
        articleRepository = nitrite.getRepository(ExArticle.class);
        contentRepository = nitrite.getRepository(ArticleContent.class);
    }

    @Override
    public void close() {
        nitrite.commit();
        nitrite.close();
    }

    @Override
    public List<ArticleType> loadContents() {
        return typeRepository
                .find()
                .toList()
                .stream()
                .map(type -> this.mapExtType(type,null))
                .collect(Collectors.toList());
    }

    private String addTypeInternal(ArticleType type, String parentId) {

       ExArticleType stored = getTypeInternal(type.getId(),parentId, type.getName());

        if (stored == null) {
            stored = new ExArticleType();
            stored.setName(type.getName());
            stored.setTypeId(type.getId());
            stored.setParentId(null);
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

        ContentService contentService = findService(ContentService.class);

        ExArticle stored = articleRepository.find(ObjectFilters.and(
                ObjectFilters.eq("articleId",article.getId()),
                ObjectFilters.eq("typeId",typeId)))
                .firstOrDefault();

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
        return typeRepository.find(ObjectFilters.and(
                ObjectFilters.eq("typeId", id),
                ObjectFilters.eq("name",name),
                ObjectFilters.eq("parentId",parentId)))
                .firstOrDefault();
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

        // 枚举子分类，并且进行处理
        List<ExArticleType> childs = typeRepository
                .find(ObjectFilters.eq("parentId",type.getParentId()))
                .toList();

        for (ExArticleType articleType: childs){
            realType.getChildren().add(mapExtType(articleType,realType));
        }

        List<ExArticle> articles = articleRepository
                .find(ObjectFilters.eq("typeId",type.getTypeId())).toList();
        for (ExArticle article: articles) {
            Article target = new Article();
            target.setTitle(article.getTitle());
            target.setType(realType);
            realType.getArticles().add(target);
        }
        return realType;
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
