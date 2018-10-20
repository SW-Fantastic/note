package org.swdc.note.app.file;

import com.overzealous.remark.Remark;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import javafx.stage.FileChooser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.entity.ArtleContext;
import org.swdc.note.app.service.ArtleService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * HTML格式导出支持
 * 写HTML，HTM文件。
 */
@Component
public class MarkedHtmlFormater extends FileFormater {

    @Autowired
    private ArtleService artleService;

    @Autowired
    private Parser parser;

    @Autowired
    private HtmlRenderer renderer;

    @Autowired
    private Remark remark;

    @Autowired
    private UIConfig config;

    private List<FileChooser.ExtensionFilter> filters = Arrays.asList(new FileChooser.ExtensionFilter("HTML格式","*.html","*.htm"));

    @Override
    public String getFormatName() {
        return "HTML文档格式（*.html）";
    }

    @Override
    public <T> T processRead(File target,Class<T> clazz) {
        System.out.println("load html");
        try {
            String source = UIUtil.readFile((InputStream)new FileInputStream(target));
            Document doc = Jsoup.parse(source);
            Elements elems = doc.getElementsByTag("img");
            Map<String,String> resource = new HashMap<>();
            for(Element elem:elems){
                String res = elem.attr("src");
                StringBuilder sb = new StringBuilder();
                Base64.Encoder  encoder = Base64.getEncoder();
                if(res.startsWith("http")){
                    HttpURLConnection connection = (HttpURLConnection)new URL(res).openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestMethod("GET");
                    connection.setUseCaches(false);
                    connection.setInstanceFollowRedirects(true);
                    connection.connect();
                    int code = connection.getResponseCode();
                    if(code == 200){
                        DataInputStream din = new DataInputStream(connection.getInputStream());
                        byte[] buff = new byte[1024];
                        while (din.read(buff) > 0){
                            String data = encoder.encodeToString(buff);
                            sb.append(data);
                        }
                        din.close();
                    }
                    connection.disconnect();
                    resource.put(""+sb.hashCode(),sb.toString());
                }else if (res.startsWith("file")){
                    String path = new URL(res).getPath();
                    File file = new File(path);
                    String data = UIUtil.readFile((InputStream)new FileInputStream(file));
                    data = encoder.encodeToString(data.getBytes());
                    resource.put(data.hashCode()+"",data);
                }else if(res.startsWith("data")){
                    String data = res.replace("data:image/png;base64,","");
                    resource.put(data.hashCode()+"",data);
                }
            }
            String markdown = remark.convertFragment(source);
            Artle artle = new Artle();
            artle.setType(null);
            artle.setTitle(doc.title());
            artle.setCreatedDate(new Date());
            ArtleContext context = new ArtleContext();
            context.setContent(markdown);
            context.setImageRes(resource);
            artle.setContext(context);
            return (T) artle;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
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
            try {
                content = "<!doctype html><html><head><meta charset='UTF-8'><style>"+config.getMdStyleContent()+"</style></head>"
                        +"<body ondragstart='return false;'>"+new String(content.getBytes(Charset.defaultCharset()),"utf8")+"</body></html>";
                if(!extName[extName.length - 1].equals("html")&&!extName[extName.length - 1].equals("htm")){
                    target = new File(target.getAbsolutePath() + ".html");
                }
                UIUtil.processWriteFile(target,content);
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public <T> void processImport(File target, T targetObj) {
        
    }

    @Override
    public List<FileChooser.ExtensionFilter> getFilters() {
        return filters;
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public boolean canWrite() {
        return true;
    }
}
