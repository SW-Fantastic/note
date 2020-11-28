package org.swdc.note.core.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;

import java.nio.file.Files;
import java.nio.file.Path;

public class SourceFormatter extends CommonContentFormatter<Article> {

    @Override
    public void save(Path file, Article entity) {
        try {
            if (Files.exists(file)) {
                Files.delete(file);
            }
            byte[] data = writeAsBytes(entity);
            Files.write(file, data);
        } catch (Exception e) {
            logger.error("fail to write file", e);
        }
    }

    @Override
    public Class<Article> getType() {
        return Article.class;
    }

    public byte[] writeAsBytes(Article article) {
        try {
            /*ArticleContent content = article.getContent();
            ArticleContent filled = new ArticleContent();
            filled.setResources(content.getResources());
            filled.setSource(content.getSource());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsBytes(filled);*/
            return new byte[0];
        } catch (Exception e) {
            logger.error("fail to render content :",e);
            return null;
        }
    }

    @Override
    public Article load(Path path) {
       /* if (!Files.exists(path)) {
            logger.error("fail to load a file which not existed :" + path.toString());
            return null;
        }
        try {
            byte[] data = Files.readAllBytes(path);
            ObjectMapper mapper = new ObjectMapper();
            ArticleContent content = mapper.readValue(data, ArticleContent.class);
            Article target = new Article();
            target.setContent(content);
            target.setTitle(path.getFileName().toString());
            target.setContentFormatter(SourceFormatter.class);
            target.setLocation(path.toString());
            return target;
        } catch (Exception e) {
            logger.error("fail to load content : " + path.toString(), e);
        }*/
        return null;
    }

    @Override
    public String getName() {
        return "Markdown 源文件";
    }

    @Override
    public String getExtension() {
        return "mdzz";
    }

    @Override
    public boolean readable() {
        return true;
    }

    @Override
    public boolean writeable() {
        return true;
    }
}
