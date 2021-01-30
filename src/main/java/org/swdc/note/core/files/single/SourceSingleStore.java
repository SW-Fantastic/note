package org.swdc.note.core.files.single;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.FileChooser;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.service.ContentService;

import java.io.File;
import java.nio.file.Files;

public class SourceSingleStore extends AbstractSingleStore {

    @Override
    public FileChooser.ExtensionFilter getFilter() {
        return new FileChooser.ExtensionFilter("源文件","*.notesource");
    }

    @Override
    public String getFileTypeName() {
        return "源文件";
    }

    @Override
    public String getExtension() {
        return "notesource";
    }

    @Override
    public void save(Article article, File target) {

        ContentService contentService = findService(ContentService.class);
        ArticleContent content = contentService.getArticleContent(article.getId());
        article.setContent(content);

        ObjectMapper mapper = new ObjectMapper();
        try {
            if (Files.exists(target.toPath())) {
                Files.delete(target.toPath());
            }
            Files.writeString(target.toPath(),mapper.writeValueAsString(article));
        } catch (Exception e) {
            logger.error("fail to write data", e);
        }

    }

    @Override
    public Article load(File file) {
        if (!Files.exists(file.toPath())) {
            return null;
        }
        try {
            String result = Files.readString(file.toPath());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(result,Article.class);
        } catch (Exception e) {
            logger.error("fail to read value", e);
            return null;
        }
    }

    @Override
    public boolean support(File file) {
        return file.getName().endsWith(this.getExtension());
    }

    @Override
    public String toString() {
        return getFileTypeName();
    }
}
