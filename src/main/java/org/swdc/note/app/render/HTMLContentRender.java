package org.swdc.note.app.render;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.swdc.note.app.event.RefreshEvent;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.DataUtil;

import javax.annotation.PostConstruct;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HTMLContentRender implements ContentRender {

    @Autowired
    private HtmlRenderer renderer;

    @Autowired
    private Parser parser;

    @Autowired
    private UIConfig config;

    @Autowired
    private Configuration markerConfig;

    private static final String name = "html";

    private String style = "";

    @PostConstruct
    public void init() throws Exception {
        StringWriter stringWriter = new StringWriter();
        Template template = new Template("styles",config.getMdStyleContent(),markerConfig);
        Map<String,Object> configsMap = new HashMap<>();
        configsMap.put("defaultFontSize", config.getRenderFontSize());
        configsMap.put("headerFontSize", config.getHeaderFontSize());
        configsMap.put("textshadow", config.getTextShadow());
        template.process(configsMap,stringWriter);

        style = stringWriter.toString();
    }

    @EventListener(RefreshEvent.class)
    public void onConfigRefresh(RefreshEvent event) throws Exception {
        if (event.getSource() != UIConfig.class) {
            return;
        }
        this.init();
    }

    @Override
    public String processBeforeRender(String source, Map<String, String> resource) {
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
            String funcData = DataUtil.compileFunc(result);
            if (funcData != null){
                // 准备图片
                funcsMap.put(result,funcData);
                source = source.replace("$"+result+"$","![func]["+result.trim()+"]");
            }
        }
        StringBuilder sb = new StringBuilder(source);
        sb.append("\n\n");
        resource.entrySet().forEach(ent->
                sb.append("[")
                        .append(ent.getKey())
                        .append("]: data:image/png;base64,")
                        .append(ent.getValue())
                        .append("\n"));
        funcsMap.entrySet().forEach(ent->
                sb.append("[")
                        .append(ent.getKey().trim())
                        .append("]: data:image/png;base64,")
                        .append(ent.getValue())
                        .append("\n"));
        return sb.toString();
    }

    @Override
    public String renderSource(String source) {
        return "<!doctype html><html><head><meta charset='UTF-8'><style>" +
                style + "</style></head>" +
                "<body ondragstart='return false;'>" +
                renderer.render(parser.parse(source)) +
                "</body></html>";

    }

    @Override
    public String processAfterRender(String html) {
        return html;
    }

    @Override
    public boolean support(String subfix) {
        return subfix.trim().toLowerCase().equals(name);
    }
}
