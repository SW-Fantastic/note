package org.swdc.note.core.render;

import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.data.DataHolder;
import freemarker.template.Template;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.slf4j.Logger;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.fx.FXResources;
import org.swdc.fx.view.Theme;
import org.swdc.note.config.AppConfig;
import org.swdc.note.config.RenderConfig;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@MultipleImplement(ContentRender.class)
public class HTMLRender extends ContentRender {

    @Inject
    private RenderConfig config = null;

    @Inject
    private FXResources resources;

    @Inject
    private AppConfig appConfig;

    @Inject
    private Logger logger;

    private String contentStyle = "";

    private DataHolder OPTIONS = null;
    private HtmlRenderer renderer = null;
    private Parser parser;

    @PostConstruct
    public void initialize() {
        try {
            Map<String, Object> configsMap = new HashMap<>();
            configsMap.put("defaultFontSize", config.getRenderFontSize());
            configsMap.put("headerFontSize", config.getHeaderFontSize());
            configsMap.put("textshadow", config.getTextShadow());

            Theme current = Theme.getTheme(appConfig.getTheme(),resources.getAssetsFolder());
            String themePath = current.getThemeFolder().getAbsoluteFile() + File.separator + "markdown.css";
            String mdStyle = Files.readString(Paths.get(themePath));
            logger.info("markdown style loaded.");
            StringWriter stringWriter = new StringWriter();

            freemarker.template.Configuration configuration = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_25);
            Template template = new Template("styles",mdStyle,configuration);
            template.process(configsMap,stringWriter);
            logger.info("markdown style proceed.");

            contentStyle = stringWriter.toString();

            OPTIONS = PegdownOptionsAdapter.flexmarkOptions(true, Extensions.ALL_WITH_OPTIONALS);
            renderer = HtmlRenderer.builder(OPTIONS).build();
            parser = Parser.builder(OPTIONS).build();
            logger.info("render is ready");
        } catch (Exception e) {
            logger.error("fail to init markdown render :", e);
        }
    }

    /*@EventListener(ConfigRefreshEvent.class)
    public void refreshStyles(ConfigRefreshEvent configRefreshEvent) {
        if (!(configRefreshEvent.getData() instanceof RenderConfig)) {
            return;
        }
        try {
            RenderConfig config  = (RenderConfig) configRefreshEvent.getData();
            Map<String, Object> configsMap = new HashMap<>();
            configsMap.put("defaultFontSize", config.getRenderFontSize());
            configsMap.put("headerFontSize", config.getHeaderFontSize());
            configsMap.put("textshadow", config.getTextShadow());
            String themePath = getThemeAssetsPath() + File.separator + "markdown.css";
            String mdStyle = Files.readString(Paths.get(themePath));
            logger.info("markdown style loaded.");
            StringWriter stringWriter = new StringWriter();

            freemarker.template.Configuration configuration = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_25);
            Template template = new Template("styles",mdStyle,configuration);
            template.process(configsMap,stringWriter);
            logger.info("markdown style proceed.");

            contentStyle = stringWriter.toString();
        } catch (Exception e) {
            logger.error("fail to refresh style", e);
        }
    }*/

    public String render(String source, Map<String, ByteBuffer> resource) {
        // 匹配双$符，在这之间的是公式
        Pattern pattern = Pattern.compile("\\$[^$]+\\$");
        Matcher matcher = pattern.matcher(source);
        Map<String,String> funcsMap = new HashMap<>();
        // 匹配到了一个
        while (matcher.find()){
            // 获取内容，转换为base64
            String result = matcher.group();
            result = result.substring(1,result.length() - 1);
            if (result.trim().equals("")){
                continue;
            }
            String funcData = compileFunc(result);
            if (funcData != null){
                // 准备图片
                funcsMap.put(result,funcData);
                source = source.replace("$"+result+"$","![func]["+result.trim()+"]");
            }
        }
        StringBuilder sb = new StringBuilder(source);
        sb.append("\n\n");
        Base64.Encoder encoder = Base64.getEncoder();
        resource.entrySet().forEach(ent->
                sb.append("[")
                        .append(ent.getKey())
                        .append("]: data:image/png;base64,")
                        .append(encoder.encodeToString(ent.getValue().array()))
                        .append("\n"));
        funcsMap.entrySet().forEach(ent->
                sb.append("[")
                        .append(ent.getKey().trim())
                        .append("]: data:image/png;base64,")
                        .append(ent.getValue())
                        .append("\n"));
        return sb.toString();
    }

    public String renderBytes(String source, Map<String, byte[]> resource) {
        // 匹配双$符，在这之间的是公式
        Pattern pattern = Pattern.compile("\\$[^$]+\\$");
        Matcher matcher = pattern.matcher(source);
        Map<String,String> funcsMap = new HashMap<>();
        // 匹配到了一个
        while (matcher.find()){
            // 获取内容，转换为base64
            String result = matcher.group();
            result = result.substring(1,result.length() - 1);
            if (result.trim().equals("")){
                continue;
            }
            String funcData = compileFunc(result);
            if (funcData != null){
                // 准备图片
                funcsMap.put(result,funcData);
                source = source.replace("$"+result+"$","![func]["+result.trim()+"]");
            }
        }
        StringBuilder sb = new StringBuilder(source);
        sb.append("\n\n");
        Base64.Encoder encoder = Base64.getEncoder();
        resource.entrySet().forEach(ent->
                sb.append("[")
                        .append(ent.getKey())
                        .append("]: data:image/png;base64,")
                        .append(encoder.encodeToString(ent.getValue()))
                        .append("\n"));
        funcsMap.entrySet().forEach(ent->
                sb.append("[")
                        .append(ent.getKey().trim())
                        .append("]: data:image/png;base64,")
                        .append(ent.getValue())
                        .append("\n"));

        return sb.toString();
    }

    public String renderHTML(String source) {
        return "<!doctype html><html><head><meta charset='UTF-8'><style>" +
                contentStyle + "</style></head>" +
                "<body ondragstart='return false;'>" +
                renderer.render(parser.parse(source)) +
                "</body></html>";
    }

    public String generateDesc(ArticleContent content) {
        String originMarkdown = this.renderBytes(content.getSource(), content.getImages());
        String html = renderHTML(originMarkdown);
        Document doc = Jsoup.parse(html);
        String desc = doc.text().replaceAll("[\\r\\n]","");
        return desc.length() > 20 ? desc.substring(0, 20): desc;
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
    public String renderAsText(Article article) {
        ArticleContent content = article.getContent();
        Map<String, byte[]> data = content.getImages();
        return renderHTML(renderBytes(content.getSource(), data));
    }

    @Override
    public byte[] renderAsBytes(Article article) {
        return renderAsText(article).getBytes();
    }
}
