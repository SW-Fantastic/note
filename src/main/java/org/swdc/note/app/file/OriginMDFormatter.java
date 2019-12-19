package org.swdc.note.app.file;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleContext;
import org.swdc.note.app.service.ArticleService;
import org.swdc.note.app.util.UIUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 提供mdxn格式的的导入导出
 */
@Component
public class OriginMDFormatter extends AbstractFormatter<Article> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private ArticleService articleService;

    @Override
    public boolean supportObject(Class type) {
        return type == Article.class;
    }

    @Override
    public Article readDocument(File file) {
        InputStream is = null;
        try {
            is =  new FileInputStream(file);
            String source = UIUtil.readFileAsText(is);
            Map<String,String> result = (Map) JSON.parse(source);
            Article article = new Article();
            article.setTitle(result.get("artleTitle"));
            article.setCreatedDate(dateFormat.parse(result.get("artleDate")));
            ArticleContext context = new ArticleContext();
            context.setImageRes((Map)JSON.parse(result.get("resource")));
            context.setContent(result.get("artleContext"));
            article.setContext(context);
            is.close();
            return article;
        }catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void writeDocument(File file, Article articleTarget) {
        String[] nameExt = file.getName().split("[.]");
        ArticleContext context = articleService.loadContext(articleTarget);
        Map<String,String> output = new HashMap<>();
        output.put("artleTitle", articleTarget.getTitle());
        output.put("artleTypeName", articleTarget.getType().getName());
        output.put("artleContext",context.getContent());
        output.put("artleDate", dateFormat.format(articleTarget.getCreatedDate()));
        output.put("resource", JSON.toJSONString(context.getImageRes()));
        String result = JSON.toJSONString(output);
        if (!nameExt[nameExt.length - 1].toLowerCase().equals(this.getFormatExtension())){
            file = new File(file.getAbsolutePath() + ".mdxn");
        }
        UIUtil.processWriteFile(file,result);
    }

    @Override
    public String getFormatName() {
        return "原始markdown数据";
    }

    @Override
    public String getFormatExtension() {
        return "mdxn";
    }

    @Override
    public boolean isBatch() {
        return false;
    }
}
