package org.swdc.note.core.proto;

import org.swdc.fx.AppComponent;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.render.FileExporter;
import org.swdc.note.core.service.ArticleService;

import java.io.File;

public abstract class URLProtoResolver extends AppComponent {

    public ArticleType resolveAsArticleSet(String url) {
        File tempFile = load(url);
        ArticleService articleService = findService(ArticleService.class);
        FileExporter exporter = articleService.getFileExporter(tempFile,true,false);
        if (exporter == null) {
            // 不支持
            return null;
        }
        ArticleType target = exporter.readTypeFile(tempFile);
        if(!tempFile.delete()) {
            tempFile.deleteOnExit();
        }
        return target;
    }

    public Article resolveAsArticle(String url) {
        File tempFile = load(url);
        ArticleService articleService = findService(ArticleService.class);
        FileExporter exporter = articleService.getFileExporter(tempFile,true,true);
        if (exporter == null) {
            // 不支持
            return null;
        }
        Article target = exporter.readFile(tempFile);
        if(!tempFile.delete()) {
            tempFile.deleteOnExit();
        }
        return target;
    }

    protected abstract File load(String url);

    public abstract boolean support(String url);

}
