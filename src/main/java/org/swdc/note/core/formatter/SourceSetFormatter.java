package org.swdc.note.core.formatter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.swdc.fx.resource.source.ArchiveFileResource;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleType;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SourceSetFormatter extends CommonContentFormatter<ArticleType> {

    public byte[] writeAsBytes(Article article) {
        try {
            ArticleContent content = article.getContent();
            ArticleContent filled = new ArticleContent();
            filled.setResources(content.getResources());
            filled.setSource(content.getSource());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsBytes(filled);
        } catch (Exception e) {
            logger.error("fail to render content :",e);
            return null;
        }
    }

    @Override
    public void save(Path file, ArticleType entity) {
        try {
            if (Files.exists(file)) {
                Files.delete(file);
            }
            ZipOutputStream zout = new ZipOutputStream(Files.newOutputStream(file));
            Set<Article> articles = entity.getArticles();
            Map<String,Article> indexFile = new HashMap<>();
            for (Article article: articles) {
                UUID uuid = UUID.randomUUID();
                ZipEntry entry = new ZipEntry(uuid.toString());
                Article output = new Article();
                output.setTitle(article.getTitle());
                output.setDesc(article.getDesc());
                output.setCreateDate(article.getCreateDate());
                indexFile.put(uuid.toString(),output);
                zout.putNextEntry(entry);
                byte[] data = writeAsBytes(article);
                zout.write(data);
                zout.closeEntry();
            }
            zout.putNextEntry(new ZipEntry(".meta-index"));
            ObjectMapper mapper = new ObjectMapper();
            byte[] data = mapper.writeValueAsBytes(indexFile);
            zout.write(data);
            zout.closeEntry();

            zout.flush();
            zout.finish();
            zout.close();
        } catch (Exception e) {
            logger.error("fail to write source set", e);
        }
    }

    @Override
    public ArticleType load(Path file) {
        try {
            URI url = ArchiveFileResource.getVirtualURI(file.toFile());
            FileSystem fs = ArchiveFileResource.createAFS(url);

            ObjectMapper mapper = new ObjectMapper();
            JavaType type = mapper.getTypeFactory().constructParametricType(Map.class, String.class, Article.class);

            Path indexFile = fs.getPath("/",".meta-index");
            InputStream in = Files.newInputStream(indexFile);
            Map<String,Article> indexMap = mapper.readValue(in,type);
            ArticleType created = new ArticleType();
            Set<Article> hashSet = new HashSet<>();
            indexMap.forEach((id, article) -> {
                try {
                    Path filePath = fs.getPath("/",id);
                    InputStream itemInput = Files.newInputStream(filePath);
                    ArticleContent articleContent = mapper.readValue(itemInput, ArticleContent.class);
                    article.setContent(articleContent);
                } catch (Exception e) {
                    logger.error("fail to load content :" + article.getTitle(), e);
                }
            });
            hashSet.addAll(indexMap.values());
            created.setName(file.getFileName().toString());
            created.setArticles(hashSet);
            in.close();
            fs.close();
            created.setFormatter(SourceFormatter.class);
            return created;
        } catch (Exception e) {
            logger.error("fail to load file:" + file, e);
        }
        return null;
    }

    @Override
    public String getName() {
        return "markdown文档集";
    }

    @Override
    public String getExtension() {
        return "mdsrc";
    }

    @Override
    public Class<ArticleType> getType() {
        return ArticleType.class;
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
