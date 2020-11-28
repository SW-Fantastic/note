package org.swdc.note.core.service;

import javafx.stage.FileChooser;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.jpa.anno.Transactional;
import org.swdc.fx.services.Service;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleResource;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.proto.URLProtoResolver;
import org.swdc.note.core.formatter.CommonContentFormatter;
import org.swdc.note.core.formatter.ContentFormatter;
import org.swdc.note.core.render.HTMLRender;
import org.swdc.note.core.repo.ArticleContentRepo;
import org.swdc.note.core.repo.ArticleRepo;
import org.swdc.note.core.repo.ArticleTypeRepo;

import java.io.File;
import java.nio.file.Paths;
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
    private ContentService contentService;

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
            contentService.saveArticleContent(content);
            return articleRepo.save(articleOld);
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
            contentService.saveArticleContent(content);

            return saved;
        }


        /*if(article.getContentFormatter() != null) {
            ContentFormatter formatter = (ContentFormatter) findComponent(article.getContentFormatter());
            article.setContent(content);
            formatter.save(Paths.get(article.getLocation()),article);
            return article;
        }*/
    }

    @Transactional
    public void deleteArticle(Article target) {
        Article article = articleRepo.getOne(target.getId());
        articleRepo.remove(article);
    }

    public Article getArticle(Long articleId) {
        return articleRepo.getOne(articleId);
    }

    public List<Article> getRecently() {
        return articleRepo.findRecently();
    }

    @Transactional
    public void deleteType(ArticleType articleType) {
        ArticleType type = typeRepo.getOne(articleType.getId());
        if (type != null) {
            typeRepo.remove(type);
        }
    }

    public List<ArticleType> getTypes() {
        return typeRepo.findRootTypes();
    }

    public List<Article> getArticles(ArticleType type) {
        return articleRepo.findByType(type);
    }

    public ContentFormatter getFormatter(File file, Class entityClass) {
        List<CommonContentFormatter> formatters = getScoped(CommonContentFormatter.class);
        for (var item : formatters) {
            if (item.support(file.toPath()) && item.getType().equals(entityClass)) {
                return item;
            }
        }
        return null;
    }

    public List<ContentFormatter> getAllFormatter(Predicate<CommonContentFormatter> predicate) {
        List<CommonContentFormatter> formatters = getScoped(CommonContentFormatter.class);
        if (predicate != null) {
            return formatters.stream()
                    .filter(predicate)
                    .collect(Collectors.toList());
        } else {
            return formatters.stream()
                    .map(ContentFormatter.class::cast)
                    .collect(Collectors.toList());
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

    public ArticleType getType(Long typeId) {
        return typeRepo.getOne(typeId);
    }

    public List<FileChooser.ExtensionFilter> getSupportedFilters(Predicate<CommonContentFormatter> predicate) {
        List<CommonContentFormatter> formatters = getScoped(CommonContentFormatter.class);
        if (predicate != null) {
            return  formatters.stream()
                    .filter(predicate)
                    .map(CommonContentFormatter::getExtensionFilter)
                    .collect(Collectors.toList());
        }
        return formatters.stream()
                .map(CommonContentFormatter::getExtensionFilter)
                .collect(Collectors.toList());
    }

}
