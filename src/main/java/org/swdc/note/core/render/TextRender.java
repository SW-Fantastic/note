package org.swdc.note.core.render;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TextRender extends ContentRender{
    @Override
    public String name() {
        return "Markdown 源文件";
    }

    @Override
    public String typeName() {
        return "markdown源文件";
    }

    @Override
    public String renderAsText(Article article) {
        try {
            ArticleContent content = article.getContent();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(content);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public byte[] renderAsBytes(Article article) {
        try {
            ArticleContent content = article.getContent();
            ArticleContent filled = new ArticleContent();
            filled.setResources(content.getResources());
            filled.setSource(content.getSource());
            filled.setArticle(article);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsBytes(filled);
        } catch (Exception e) {
            logger.error("fail to render content :",e);
            return null;
        }
    }

    @Override
    public void renderAsFile(Article article, Path file) {
        try {
            if (Files.exists(file)) {
                Files.delete(file);
            }
            byte[] data = renderAsBytes(article);
            Files.write(file, data);
        } catch (Exception e) {
            logger.error("fail to write file", e);
        }
    }

    @Override
    public void renderAllArticles(ArticleType type, Path file) {
        try {
            if (Files.exists(file)) {
                Files.delete(file);
            }
            ZipOutputStream zout = new ZipOutputStream(Files.newOutputStream(file));
            Set<Article> articles = type.getArticles();
            for (Article article: articles) {
                ZipEntry entry = new ZipEntry(article.getTitle());
                zout.putNextEntry(entry);
                byte[] data = renderAsBytes(article);
                zout.write(data);
                zout.closeEntry();
            }
            zout.flush();
            zout.finish();
            zout.close();
        } catch (Exception e) {
            logger.error("fail to write source set", e);
        }
    }

    @Override
    public String subfix() {
        return "mdzz";
    }

    @Override
    public String typeSubfix() {
        return "mdsrc";
    }

}
