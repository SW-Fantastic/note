package org.swdc.note.app.file;

import com.overzealous.remark.Remark;
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

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class HTMLFormatter extends AbstractFormatter<Article> {

    @Autowired
    private Remark remark;

    @Autowired
    private ArticleService articleService;

    @Override
    public boolean supportObject(Class type) {
        return type == Article.class;
    }

    @Override
    public Article readDocument(File file) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
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
                if(res == null || res.equals("")) {
                    continue;
                }
                Base64.Encoder  encoder = Base64.getEncoder();
                if(res.startsWith("http")){
                    byte[] data = DataUtil.loadHttpData(res);
                    URL url = new URL(res);
                    String fileName = url.getFile();
                    resource.put(fileName.substring(1), encoder.encodeToString(data));
                }else if (res.startsWith("file")){
                    String path = new URL(res).getPath();
                    File localFile = new File(path);
                    inputStream = new FileInputStream(localFile);
                    byte[] data = UIUtil.readFile(inputStream);
                    String image = encoder.encodeToString(data);
                    resource.put(file.getName(),image);
                }else if(res.startsWith("data")){
                    index ++;
                    String data = res.replace("data:image/png;base64,","");
                    resource.put("Image "+ index,data);
                }else {
                    File localFile = new File(file.getAbsoluteFile().getParentFile().getPath() + File.separator + URLDecoder.decode(res,"utf8"));
                    if (localFile.exists()) {
                        inputStream = new FileInputStream(localFile);
                        index ++;
                        byte[] data = UIUtil.readFile(inputStream);
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
            return article;
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
    public void writeDocument(File file, Article article) {
        String name = file.getAbsolutePath();
        String[] extName = name.split("[.]");
        ArticleContext context = articleService.loadContext(article);
        Map<String,String> resource = context.getImageRes();
        ContentRender render = articleService.getRender("html");
        String content = render.processBeforeRender(context.getContent(),resource);
        content = render.renderSource(content);
        content = render.processAfterRender(content);
        try {
            if(!extName[extName.length - 1].equals("html")&&!extName[extName.length - 1].equals("htm")){
                    file = new File(file.getAbsolutePath() + ".html");
            }
            UIUtil.processWriteFile(file,content);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getFormatName() {
        return "HTML标记文本";
    }

    @Override
    public String getFormatExtension() {
        return "html";
    }

    @Override
    public boolean isBatch() {
        return false;
    }
}
