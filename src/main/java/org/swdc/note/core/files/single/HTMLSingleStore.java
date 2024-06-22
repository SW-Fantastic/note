package org.swdc.note.core.files.single;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;
import jakarta.inject.Inject;
import javafx.stage.FileChooser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleEditorType;
import org.swdc.note.core.render.HTMLRender;
import org.swdc.note.core.service.ContentService;
import org.swdc.note.ui.component.blocks.BlockData;
import org.swdc.note.ui.component.blocks.ImageBlock;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@MultipleImplement(AbstractSingleStore.class)
public class HTMLSingleStore extends AbstractSingleStore {

    @Inject
    private HTMLRender htmlRender;

    @Inject
    private Logger logger;

    @Inject
    private ContentService contentService;

    @Override
    public FileChooser.ExtensionFilter getFilter() {
        return new FileChooser.ExtensionFilter("HTML","*.html");
    }

    @Override
    public String getFileTypeName() {
        return "HTML文本";
    }

    @Override
    public String getExtension() {
        return "html";
    }

    @Override
    public void save(Article article, File target) {


        ArticleContent content = Optional.ofNullable(article.getContent())
                .orElse(contentService.getArticleContent(article.getId()));

        String rendered = "";

        if (article.getEditorType() == ArticleEditorType.BlockEditor) {
            String blockData = content.getSource();
            ObjectMapper mapper = new ObjectMapper();
            try {
                JavaType type = mapper.getTypeFactory()
                        .constructParametricType(List.class, BlockData.class);

                List<BlockData> data = mapper.readValue(blockData,type);
                Map<String,byte[]> images = content.getImages();

                StringBuilder builder = new StringBuilder();
                for (BlockData item : data) {
                    if (ImageBlock.class.getName().equals(item.getType())) {
                        Map<String,String> header = (Map<String, String>) item.getContent();
                        String name = header.get("name");
                        if (images.containsKey(name)) {
                            builder.append("\n![desc][" + name + "]\n");
                        }
                    } else {
                        builder.append("\n").append(item.getSource());
                    }
                }
                String source = htmlRender.renderBytes(builder.toString(),images);
                rendered = htmlRender.renderHTML(source);
            } catch (Exception e) {
                logger.error("failed to load content : ", e);
            }
        } else {
            rendered = htmlRender.renderHTML(htmlRender.renderBytes(content.getSource(), content.getImages()));
        }

        try {
            if (Files.exists(target.toPath())) {
                Files.delete(target.toPath());
            }
            Files.writeString(target.toPath(),rendered);
        } catch (Exception e) {
            logger.error("fail to write file",e);
        }
    }

    @Override
    public Article load(File file) {
        if (!file.exists()) {
            return null;
        }
        if (!file.getName().toLowerCase().endsWith("html")) {
            if (!file.getName().toLowerCase().endsWith("htm")) {
                return null;
            }
        }
        Path filePath = file.toPath();
        Remark remark = new Remark(Options.markdown());
        try {
            String source = Files.readString(filePath);
            Document doc = Jsoup.parse(source);
            Elements links = doc.body().getElementsByTag("a");
            for (Element elem : links) {
                elem.tagName("span").attributes().remove("href");
            }
            Elements elems = doc.getElementsByTag("img");

            Map<String, byte[]> resource = new HashMap<>();
            int index = 0;
            for (Element elem : elems) {
                String res = elem.attr("src");
                if (res.isBlank() && elem.hasAttr("data-src")) {
                    res = elem.attr("data-src");
                }
                if (res == null || res.equals("")) {
                    continue;
                }
                if (res.startsWith("file")) {
                    String path = new URL(res).getPath();
                    File localFile = new File(path);
                    byte[] data = Files.readAllBytes(Paths.get(localFile.getAbsolutePath()));
                    resource.put(localFile.getName(), data);
                } else if (res.startsWith("data")) {
                    index++;
                    String content = res.replace("data:image/png;base64,", "");
                    byte[] data = Base64.getDecoder().decode(content);
                    resource.put("Image " + index, data);
                } else {
                    File localFile = new File(filePath.toFile().getAbsoluteFile().getParentFile().getPath() + File.separator + URLDecoder.decode(res, "utf8"));
                    if (localFile.exists()) {
                        index++;
                        byte[] data = Files.readAllBytes(Paths.get(localFile.getAbsolutePath()));
                        resource.put("Image " + index, data);
                    }
                }
            }
            String markdown = remark.convertFragment(doc.toString());


            ArticleContent content = new ArticleContent();
            content.setImages(resource);
            content.setSource(markdown);

            Article article = new Article();

            article.setTitle(filePath.getFileName().toString());
            article.setCreateDate(new java.util.Date());
            article.setSingleStore(HTMLSingleStore.class);
            article.setContent(content);
            article.setFullPath(file.getAbsolutePath());

            return article;
        } catch (Exception e) {
            logger.error("fail to read html file", e);
        }
        return null;
    }

    @Override
    public boolean support(File file) {
        return file.getName().toLowerCase().endsWith("htm") ||
                file.getName().toLowerCase().endsWith("html");
    }

    @Override
    public String toString() {
        return getFileTypeName();
    }
}
