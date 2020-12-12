package org.swdc.note.core.files.single;

import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;
import javafx.stage.FileChooser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.proto.HttpURLResolver;
import org.swdc.note.core.render.HTMLRender;
import org.swdc.note.core.service.ContentService;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HTMLSingleStore extends AbstractSingleStore {

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

        HTMLRender render = findComponent(HTMLRender.class);

        ContentService contentService = findService(ContentService.class);
        ArticleContent content = Optional.ofNullable(article.getContent())
                .orElse(contentService.getArticleContent(article.getId()));
        String rendered = render.renderHTML(render.renderBytes(content.getSource(), content.getImages()));

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
                if (res.startsWith("http")) {
                    byte[] data = HttpURLResolver.loadHttpData(res);
                    URL url = new URL(res);
                    String fileName = url.getFile();
                    resource.put(fileName.substring(1), data);
                } else if (res.startsWith("file")) {
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
