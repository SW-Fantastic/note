package org.swdc.note.core.service;

import javafx.stage.FileChooser;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.services.Service;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleResource;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.render.ContentRender;
import org.swdc.note.core.render.HTMLRender;
import org.swdc.note.core.repo.ArticleRepo;
import org.swdc.note.core.repo.ArticleTypeRepo;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ArticleService extends Service {

    @Aware
    private ArticleTypeRepo typeRepo = null;

    @Aware
    private ArticleRepo articleRepo = null;

    @Aware
    private HTMLRender render = null;

    public boolean createType(ArticleType type) {
        try {
            if (type.getId() != null) {
                return false;
            }
            if (type.getName().isBlank()) {
                return false;
            }
            List<ArticleType> typeEx = typeRepo.findByTypeName(type.getName());
            if (typeEx == null || typeEx.size() == 0) {
                typeRepo.save(type);
                return true;
            }
            return false;
        } catch (Exception ex) {
            logger.error("fail to create article type: " + type.getName(), ex);
            return false;
        }
    }

    public boolean saveArticle(Article article, ArticleContent content) {
        if (article.getId() != null) {
            Article articleOld = articleRepo.getOne(article.getId());
            if (articleOld == null) {
                Article refreshed = new Article();
                refreshed.setType(article.getType());
                ArticleContent contentRef = new ArticleContent();
                ArticleResource resource = content.getResources();
                contentRef.setSource(content.getSource());
                contentRef.setResources(resource);
                refreshed.setContent(contentRef);
                refreshed.setTitle(article.getTitle());
                return saveArticle(refreshed,refreshed.getContent());
            }
            if (!(article.getTitle() == null || article.getTitle().isBlank() || article.getTitle().isEmpty())) {
                articleOld.setTitle(article.getTitle());
            }
            if (article.getType() != null) {
                articleOld.setType(article.getType());
            }
            String desc = render.generateDesc(content);
            articleOld.setDesc(desc);
            articleOld.setCreateDate(new Date());

            ArticleContent contentOld = articleOld.getContent();
            if (content.getSource() != null && !content.getSource().isEmpty() && !content.getSource().isBlank()) {
                contentOld.setSource(content.getSource());
            }
            if (content.getResources() != null){
                contentOld.setResources(content.getResources());
            }

            try {
                articleRepo.save(article);
                return true;
            } catch (Exception ex) {
                logger.error("failed to save article: ",ex);
                return false;
            }

        } else {
            if (article.getTitle() == null || article.getTitle().isBlank() || article.getTitle().isEmpty()) {
                return false;
            }
            if (article.getType() == null) {
                return false;
            }
            String desc = render.generateDesc(content);
            article.setDesc(desc);
            article.setCreateDate(new Date());
            if (content.getSource() == null || content.getSource().isEmpty() || content.getSource().isBlank()) {
                return false;
            }
            if (content.getResources() == null){
                return false;
            }
            article.setContent(content);
            try {
                articleRepo.save(article);
                return true;
            } catch (Exception ex) {
                logger.error("failed to save article: ",ex);
                return false;
            }
        }

    }

    public void deleteArticle(Long articleId) {
        Article article = articleRepo.getOne(articleId);
        articleRepo.remove(article);
    }

    public Article getArticle(Long articleId) {
        return articleRepo.getOne(articleId);
    }

    public List<Article> getRecently() {
        return articleRepo.findRecently();
    }

    public void deleteType(Long articleTypeId) {
        ArticleType type = typeRepo.getOne(articleTypeId);

        if (type != null) {
            typeRepo.remove(type);
        }
    }

    public List<ArticleType> getTypes() {
        return typeRepo.getAll();
    }

    public List<Article> getArticles(ArticleType type) {
        return articleRepo.findByType(type);
    }

    public ContentRender getRender(File export) {
        List<ContentRender> renders = getScoped(ContentRender.class);
        for (var render: renders) {
            if (render.support(export)) {
                return render;
            }
        }
        return null;
    }

    public List<FileChooser.ExtensionFilter> getRenderFilters() {
        List<ContentRender> renders = getScoped(ContentRender.class);
        return renders.stream().map(ContentRender::getFilters).collect(Collectors.toList());
    }

    public List<ContentRender> getAllRenders() {
        List<ContentRender> renders = getScoped(ContentRender.class);
        return renders;
    }

}
