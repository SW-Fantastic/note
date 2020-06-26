package org.swdc.note.core.formatter;

import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.data.DataHolder;
import freemarker.template.Template;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.swdc.fx.anno.Aware;
import org.swdc.note.config.RenderConfig;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleResource;
import org.swdc.note.core.proto.HttpURLResolver;
import org.swdc.note.core.render.HTMLRender;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class HTMLFormatter extends CommonContentFormatter<Article> {

    @Aware
    private RenderConfig config = null;

    @Aware
    private HTMLRender render = null;

    @Override
    public boolean writeable() {
        return true;
    }

    @Override
    public boolean readable() {
        return true;
    }

    @Override
    public Class<Article> getType() {
        return Article.class;
    }

    /**
     * LateXMath公式生成Base64图片
     * @param funcStr 公式
     * @return 字符串
     * @throws Exception
     */
    public String compileFunc(String funcStr) {
        try {
            TeXFormula formula = new TeXFormula(funcStr);
            BufferedImage img = (BufferedImage) formula.createBufferedImage(funcStr, TeXConstants.STYLE_DISPLAY,18, Color.BLACK,Color.WHITE);
            ByteArrayOutputStream bot = new ByteArrayOutputStream();
            ImageIO.write(img,"PNG",bot);
            byte[] buffer = bot.toByteArray();
            return Base64.getEncoder().encodeToString(buffer);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public void save(Path path, Article article) {
        try {
            if (Files.exists(path)) {
                Files.delete(path);
            }
            byte[] data = renderAsBytes(article);
            Files.write(path, data);
        } catch (Exception e) {
            logger.error("fail to write file", e);
        }
    }

    public byte[] renderAsBytes(Article article) {
        return renderAsText(article).getBytes();
    }

    public String renderAsText(Article article) {
        ArticleContent content = article.getContent();
        Map<String, byte[]> data = content.getResources().getImages();
        return render.renderHTML(render.renderBytes(content.getSource(), data));
    }

    @Override
    public Article load(Path filePath) {
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
            ArticleResource contentResource = new ArticleResource();
            contentResource.setImages(resource);
            content.setResources(contentResource);
            content.setSource(markdown);
            Article article = new Article();
            article.setContent(content);
            article.setTitle(filePath.getFileName().toString());
            article.setCreateDate(new java.util.Date());
            article.setContentFormatter(HTMLFormatter.class);
            article.setLocation(filePath.toString());
            return article;
        } catch (Exception e) {
            logger.error("fail to read html file", e);
        }
        return null;
    }

    @Override
    public String getName() {
        return "HTML文档";
    }

    @Override
    public String getExtension() {
        return "html";
    }
}
