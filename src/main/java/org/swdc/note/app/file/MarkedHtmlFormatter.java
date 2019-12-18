package org.swdc.note.app.file;

import com.overzealous.remark.Remark;
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
import org.swdc.note.app.util.DataUtil;
import org.swdc.note.app.util.UIUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
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
    public boolean supportSingleExtension(String extensionName) {
        return extensionName.toLowerCase().trim().equals("html");
    }

    @Override
    public boolean supportMultiExtension(String extension) {
        return false;
    }

    @Override
    public <T> T processRead(File target,Class<T> clazz) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(target);
            String source = UIUtil.readFileAsText(inputStream);
            Document doc = Jsoup.parse(source);
            Elements links = doc.body().getElementsByTag("a");
            for (Element elem: links) {
                elem.tagName("span").attributes().remove("href");
            }
            Elements elems = doc.getElementsByTag("img");
            Map<String,String> resource = new HashMap<>();
            int index = 0;
            for(Element elem:elems){
                String res = elem.attr("src");
                Base64.Encoder  encoder = Base64.getEncoder();
                if(res.startsWith("http")){
                    byte[] data = DataUtil.loadHttpData(res);
                    URL url = new URL(res);
                    String file = url.getFile();
                    resource.put(file.substring(1), encoder.encodeToString(data));
                }else if (res.startsWith("file")){
                    String path = new URL(res).getPath();
                    File file = new File(path);
                    byte[] data = UIUtil.readFile(new FileInputStream(file));
                    String image = encoder.encodeToString(data);
                    resource.put(file.getName(),image);
                }else if(res.startsWith("data")){
                    index ++;
                    String data = res.replace("data:image/png;base64,","");
                    resource.put("Image "+ index,data);
                }else {
                    File file = new File(target.getAbsoluteFile().getParentFile().getPath() + File.separator + URLDecoder.decode(res,"utf8"));
                    if (file.exists()) {
                        index ++;
                        byte[] data = UIUtil.readFile(new FileInputStream(file));
                        String image = encoder.encodeToString(data);
                        resource.put("Image "+ index,image);
                    }
                }
            }
            String markdown = remark.convertFragment(doc.toString());
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
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (Exception ex) {
                return null;
            }
        }
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
