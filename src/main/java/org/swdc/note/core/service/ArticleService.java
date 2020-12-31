package org.swdc.note.core.service;

import javafx.stage.FileChooser;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.jpa.anno.Transactional;
import org.swdc.fx.services.Service;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.files.SingleStorage;
import org.swdc.note.core.files.factory.AbstractStorageFactory;
import org.swdc.note.core.files.single.AbstractSingleStore;
import org.swdc.note.core.proto.URLProtoResolver;
import org.swdc.note.core.render.HTMLRender;
import org.swdc.note.core.repo.ArticleRepo;
import org.swdc.note.core.repo.ArticleTypeRepo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ArticleService extends Service {

    @Aware
    private ArticleTypeRepo typeRepo = null;

    @Aware
    private ArticleRepo articleRepo = null;

    @Aware
    private HTMLRender render = null;

    @Aware
    private ContentService contentService = null;

    @Aware
    private IndexorService indexorService = null;

    @Transactional
    public ArticleContent getContentOf(Article article) {
        if (article.getId() == null) {
            return null;
        }
        return contentService.getArticleContent(article.getId());
    }

    @Transactional
    public ArticleType createType(ArticleType type) {
        return this.saveType(type);
    }

    @Transactional
    public ArticleType saveType(ArticleType type) {
        if (type == null) {
            return null;
        }
        if (type.getId() == null) {
            List<ArticleType> typeEx = typeRepo.findByTypeName(type.getName());
            if (typeEx == null || typeEx.size() == 0) {
                return typeRepo.save(type);
            }
        }
        if (type.getName() == null || type.getName().isBlank() || type.getName().isEmpty()) {
            return null;
        }
        return typeRepo.save(type);
    }

    public List<Article> searchByTitle(String title) {
        return articleRepo.searchByTitle(title);
    }

    @Transactional
    public Article saveArticle(Article article, ArticleContent content) {
        if (article.getId() != null) {

            Article articleOld = articleRepo.getOne(article.getId());

            if (!(article.getTitle() == null || article.getTitle().isBlank() || article.getTitle().isEmpty())) {
                articleOld.setTitle(article.getTitle());
            }
            if (article.getType() != null) {
                articleOld.setType(article.getType());
            }

            String desc = render.generateDesc(content);
            articleOld.setDesc(desc);
            articleOld.setCreateDate(new Date());

            content.setArticleId(article.getId());
            content.setTypeId(article.getType().getId());
            contentService.saveArticleContent(content);
            Article saved = articleRepo.save(articleOld);
            indexorService.updateIndex(article,content);
            return saved;
        } else {
            if (article.getTitle() == null || article.getTitle().isBlank() || article.getTitle().isEmpty()) {
                return null;
            }
            if (article.getType() == null) {
                return null;
            }
            String desc = render.generateDesc(content);
            article.setDesc(desc);
            article.setCreateDate(new Date());
            if (content.getSource() == null || content.getSource().isEmpty() || content.getSource().isBlank()) {
                return null;
            }
            Article saved = articleRepo.save(article);

            content.setArticleId(saved.getId());
            content.setTypeId(saved.getType().getId());
            contentService.saveArticleContent(content);
            indexorService.createIndex(article,content);

            return saved;
        }

    }

    @Transactional
    public void deleteArticle(Article target) {
        Article article = articleRepo.getOne(target.getId());
        articleRepo.remove(article);
        int effect = contentService.removeContent(article.getId());
        logger.info(target.getId() + " has deleted, " + effect + " effected");
        indexorService.removeIndex(article);
    }

    public Article getArticle(String articleId) {
        return articleRepo.getOne(articleId);
    }

    public List<Article> getRecently() {
        return articleRepo.findRecently();
    }

    /**
     * 删除Type
     *
     * @param articleType
     */
    @Transactional
    public void deleteType(ArticleType articleType) {
        ArticleType type = typeRepo.getOne(articleType.getId());
        if (type != null) {
            List<String> ids = getChildTypeId(articleType,null);
            typeRepo.remove(type);
            for (String typeId: ids) {
                indexorService.removeIndex(typeId);
                contentService.removeByType(typeId);
            }
        }
    }

    private List<String> getChildTypeId(ArticleType type,List<String> ids) {
        if (ids == null) {
            ids = new ArrayList<>();
        }
        List<ArticleType> child = type.getChildren();
        for (ArticleType item: child) {
            ids.add(item.getId());
            if (item.getChildren() != null) {
                getChildTypeId(item,ids);
            }
        }
        return ids;
    }

    public List<ArticleType> getTypes() {
        return typeRepo.findRootTypes();
    }

    public List<Article> getArticles(ArticleType type) {
        return articleRepo.findByType(type);
    }

    /*public ContentFormatter getFormatter(File file, Class entityClass) {
        List<CommonContentFormatter> formatters = getScoped(CommonContentFormatter.class);
        for (var item : formatters) {
            if (item.support(file.toPath()) && item.getType().equals(entityClass)) {
                return item;
            }
        }
        return null;
    }*/

    public List<AbstractStorageFactory> getAllExternalStorage(Predicate<AbstractStorageFactory> predicate) {
        List<AbstractStorageFactory> formatters = getScoped(AbstractStorageFactory.class);
        if (predicate != null) {
            return formatters.stream()
                    .filter(predicate)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>(formatters);
        }
    }

    public URLProtoResolver getURLResolver(String url) {
        List<URLProtoResolver> resolvers = getScoped(URLProtoResolver.class);
        for (URLProtoResolver resolver: resolvers){
            if (resolver.support(url)) {
                return resolver;
            }
        }
        return null;
    }

    public ArticleType getType(String typeId) {
        return typeRepo.getOne(typeId);
    }

    public List<FileChooser.ExtensionFilter> getSupportedFilters(Predicate<AbstractSingleStore> predicate) {
        List<AbstractSingleStore> singleStores = getScoped(AbstractSingleStore.class);
        if (predicate != null) {
            return  singleStores.stream()
                    .filter(predicate)
                    .map(AbstractSingleStore::getFilter)
                    .collect(Collectors.toList());
        }
        return singleStores.stream()
                .map(AbstractSingleStore::getFilter)
                .collect(Collectors.toList());
    }

    public List<SingleStorage> getSingleStore(Predicate<AbstractSingleStore> predicate) {
        List<AbstractSingleStore> singleStores = getScoped(AbstractSingleStore.class);
        if (predicate != null) {
            return  singleStores.stream()
                    .filter(predicate)
                    .collect(Collectors.toList());
        }
        return singleStores.stream()
                .collect(Collectors.toList());
    }

}
