package org.swdc.note.core.service;

import jakarta.inject.Inject;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.swdc.data.StatelessHelper;
import org.swdc.data.anno.Transactional;
import org.swdc.dependency.annotations.With;
import org.swdc.note.core.aspect.RefreshAspect;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.files.SingleStorage;
import org.swdc.note.core.files.StorageFactory;
import org.swdc.note.core.files.single.AbstractSingleStore;
import org.swdc.note.core.render.HTMLRender;
import org.swdc.note.core.repo.ArticleRepo;
import org.swdc.note.core.repo.ArticleTypeRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@With(aspectBy = RefreshAspect.class)
public class ArticleService  {

    @Inject
    private List<StorageFactory> storageFactories;


    @Inject
    private List<AbstractSingleStore> singleStores;

    @Inject
    private Logger logger;

    @Inject
    private ArticleTypeRepo typeRepo = null;

    @Inject
    private ArticleRepo articleRepo = null;

    @Inject
    private HTMLRender render = null;

    @Inject
    private ContentService contentService = null;

    @Inject
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
        return StatelessHelper.stateless(
                this.saveType(type)
        );
    }

    @Transactional
    public ArticleType saveType(ArticleType type) {
        if (type == null) {
            return null;
        }
        if (type.getId() == null) {
            List<ArticleType> typeEx = typeRepo.findByTypeName(type.getName());
            if (typeEx == null || typeEx.size() == 0) {
                return StatelessHelper.stateless(
                        typeRepo.save(type)
                );
            }
        }
        if (type.getName() == null || type.getName().isBlank() || type.getName().isEmpty()) {
            return null;
        }
        return StatelessHelper.stateless(
                typeRepo.save(type)
        );
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
            return StatelessHelper.stateless(
                    saved
            );
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

            return StatelessHelper.stateless(
                    saved
            );
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
            List<String> ids = getChildTypeId(type,null);
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
        if (child == null) {
            return ids;
        }
        for (ArticleType item: child) {
            ids.add(item.getId());
            if (item.getChildren() != null) {
                getChildTypeId(item,ids);
            }
        }
        return ids;
    }

    @Transactional
    public List<ArticleType> getTypes() {
        List<ArticleType> types = typeRepo.findRootTypes();
        if (types == null || types.isEmpty()) {
            return Collections.emptyList();
        }
        return types.stream()
                .map(StatelessHelper::stateless)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Article> getArticles(ArticleType type) {
        if (type == null) {
            return Collections.emptyList();
        }
        List<Article> articles = articleRepo.findByType(type.getId());
        if (articles == null || articles.isEmpty()) {
            return Collections.emptyList();
        }
        return articles.stream()
                .map(StatelessHelper::stateless)
                .collect(Collectors.toList());
    }


    public List<StorageFactory> getAllExternalStorage(Predicate<StorageFactory> predicate) {
        if (predicate != null) {
            return storageFactories.stream()
                    .filter(predicate)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>(storageFactories);
        }
    }


    public ArticleType getType(String typeId) {
        return typeRepo.getOne(typeId);
    }

    public List<FileChooser.ExtensionFilter> getSupportedFilters(Predicate<AbstractSingleStore> predicate) {
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
        if (predicate != null) {
            return  singleStores.stream()
                    .filter(predicate)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>(singleStores);
    }

    public SingleStorage getSingleStoreBy(Class<? extends SingleStorage> type) {
        for (SingleStorage storage : singleStores) {
            if (storage.getClass() == type) {
                return storage;
            }
        }
        return null;
    }

}
