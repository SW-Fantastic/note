package org.swdc.note.core.proto;

import org.swdc.fx.AppComponent;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.service.ArticleService;

import java.io.File;

public abstract class URLProtoResolver extends AppComponent {

    public ArticleType resolveAsArticleSet(String url) {
        /*File tempFile = load(url);
        ArticleService articleService = findService(ArticleService.class);
        ContentFormatter formatter = articleService.getFormatter(tempFile,ArticleType.class);
        if (formatter == null || !formatter.readable()) {
            // 不支持
            return null;
        }
        ArticleType target = (ArticleType) formatter.load(tempFile.toPath());
        if(!tempFile.delete()) {
            tempFile.deleteOnExit();
        }*/
        return null;
    }

    public Article resolveAsArticle(String url) {
        /*File tempFile = load(url);
        ArticleService articleService = findService(ArticleService.class);
        ContentFormatter formatter = articleService.getFormatter(tempFile,Article.class);
        if (formatter == null || !formatter.readable()) {
            // 不支持
            return null;
        }
        Article target = (Article) formatter.load(tempFile.toPath());
        if(!tempFile.delete()) {
            tempFile.deleteOnExit();
        }*/
        return null;
    }

    protected abstract File load(String url);

    public abstract boolean support(String url);

}
