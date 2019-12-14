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
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleContext;
import org.swdc.note.app.render.ContentRender;
import org.swdc.note.app.service.ArticleService;
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
public class MarkedHtmlFormatter extends FileFormatter {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private Remark remark;

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
            Article article = new Article();
            article.setType(null);
            article.setTitle(doc.title());
            article.setCreatedDate(new Date());
            ArticleContext context = new ArticleContext();
            context.setContent(markdown);
            context.setImageRes(resource);
            article.setContext(context);
            return (T) article;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void processWrite(File target, Object targetObj) {
        String name = target.getAbsolutePath();
        String[] extName = name.split("[.]");
        if(targetObj instanceof Article){
            Article article = (Article)targetObj;
            ArticleContext context = articleService.loadContext(article);
            Map<String,String> resource = context.getImageRes();
            ContentRender render = articleService.getRender("html");
            String content = render.processBeforeRender(context.getContent(),resource);
            content = render.renderSource(content);
            content = render.processAfterRender(content);
            try {
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
