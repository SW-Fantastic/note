package org.swdc.note.app.file;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.entity.ArtleContext;
import org.swdc.note.app.service.ArtleService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * HTML格式导出支持
 * 写HTML，HTM文件。
 */
@Component
public class MarkedHtmlProcesser extends FileFormater {

    @Autowired
    private ArtleService artleService;

    @Autowired
    private Parser parser;

    @Autowired
    private HtmlRenderer renderer;

    @Autowired
    private UIConfig config;

    @Override
    public String getFormatName() {
        return "HTML文档格式（*.html）";
    }

    @Override
    public <T> T processRead(File target) {
        throw new RuntimeException("此格式不支持直接读取。");
    }

    @Override
    public void processWrite(File target, Object targetObj) {
        String name = target.getAbsolutePath();
        String[] extName = name.split("[.]");
        if(targetObj instanceof Artle){
            Artle artle = (Artle)targetObj;
            ArtleContext context = artleService.loadContext(artle);
            Map<String,String> resource = context.getImageRes();
            StringBuilder sb = new StringBuilder();
            sb.append("\r\n");
            resource.entrySet().forEach(ent->
                    sb.append("[")
                            .append(ent.getKey())
                            .append("]: data:image/png;base64,")
                            .append(ent.getValue())
                            .append("\n"));
            String content = renderer.render(parser.parse(context.getContent()+"\n"+sb.toString()));
            content = "<!doctype html><html><head><meta charset='UTF-8'><style>"+config.getMdStyleContent()+"</style></head>"
                    +"<body ondragstart='return false;'>"+content+"</body></html>";
            if(!extName[extName.length - 1].equals("html")&&!extName[extName.length - 1].equals("htm")){
                target = new File(target.getAbsolutePath() + ".html");
            }
            UIUtil.processWriteFile(target,content);
        }

    }

    @Override
    public List<FileChooser.ExtensionFilter> getFilters() {
        return Arrays.asList(new FileChooser.ExtensionFilter("HTML格式","*.html","*.htm"));
    }
}
