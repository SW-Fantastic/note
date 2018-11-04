package org.swdc.note.app.file;

import com.alibaba.fastjson.JSON;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleContext;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.service.ArticleService;
import org.swdc.note.app.service.TypeService;
import org.swdc.note.app.util.UIUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *  用来读写mdxn（mdzz）格式的markdown数据，
 *  此数据是json格式的数据，由本工程创建使用。
 */
@Component
public class SwMarkdownFormatter extends FileFormatter {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private TypeService typeService;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private List<FileChooser.ExtensionFilter> filters = Arrays.asList(new FileChooser.ExtensionFilter("文档原始数据","*.mdxn"),
            new FileChooser.ExtensionFilter("原始数据集","*.mdzz"));

    @Override
    public String getFormatName() {
        return "笔记默认格式（*.mdxn）（*.mdzz)";
    }

    @Override
    public <T> T processRead(File target,Class<T> clazz) {
        String name = target.getAbsolutePath();
        String[] extName = name.split("[.]");
        if(extName[extName.length - 1].equals("mdxn") && clazz.equals(Article.class)){
            try {
                String source = UIUtil.readFile((InputStream)new FileInputStream(target));
                Map<String,String> result = (Map) JSON.parse(source);
                Article article = new Article();
                article.setTitle(result.get("artleTitle"));
                article.setCreatedDate(dateFormat.parse(result.get("artleDate")));
                ArticleContext context = new ArticleContext();
                context.setImageRes((Map)JSON.parse(result.get("resource")));
                context.setContent(result.get("artleContext"));
                article.setContext(context);
                return (T) article;
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public void processWrite(File target,Object targetObj) {
        String name = target.getName();
        String[] nameExt = name.split("[.]");
        if(targetObj instanceof Article){
            Article articleTarget = (Article)targetObj;
            ArticleContext context = articleService.loadContext(articleTarget);
            Map<String,String> output = new HashMap<>();
            output.put("artleTitle", articleTarget.getTitle());
            output.put("artleTypeName", articleTarget.getType().getName());
            output.put("artleContext",context.getContent());
            output.put("artleDate", dateFormat.format(articleTarget.getCreatedDate()));
            output.put("resource", JSON.toJSONString(context.getImageRes()));
            String result = JSON.toJSONString(output);
            if (!nameExt[nameExt.length - 1].equals("mdxn")){
                target = new File(target.getAbsolutePath() + ".mdxn");
            }
            UIUtil.processWriteFile(target,result);
        }else if(targetObj instanceof ArticleType){
            if (!nameExt[nameExt.length - 1].equals("mdzz")){
                target = new File(target.getAbsolutePath() + ".mdzz");
            }
            ArticleType type = (ArticleType) targetObj;
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(target))){
                writeType(type,zos,"source/"+type.getName());
                zos.flush();
            }catch (Exception e){

            }
        }
    }

    @Override
    public <T> void processImport(File target, T targetObj) {
        if(!(targetObj instanceof ArticleType)){
            return;
        }
        ArticleType type = (ArticleType)targetObj;
        try (ZipFile zipFile = new ZipFile(target)){
            Enumeration<? extends ZipEntry> zipEnum = zipFile.entries();
            while (zipEnum.hasMoreElements()){
                ZipEntry ent = zipEnum.nextElement();
                processImport(ent,type,zipFile);
            }
            typeService.addType(type);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public <T> void processImport(ZipEntry ent, ArticleType type, ZipFile zipFile) throws Exception{
        if(ent.isDirectory() && !ent.getName().equals("source") && !ent.getName().equals(type.getName())){
            ArticleType subType = new ArticleType();
            subType.setName(ent.getName().split("[/\\\\]")[ent.getName().split("[/\\\\]").length - 1]);
            subType.setParentType(type);
            subType.setArticles(new ArrayList<>());
            typeService.addType(subType);
        }else{
            InputStream in = zipFile.getInputStream(ent);
            Article article = new Article();
            String source = UIUtil.readFile(in);
            Map<String,String> result = (Map)JSON.parse(source);
            article.setTitle(result.get("artleTitle"));
            article.setCreatedDate(dateFormat.parse(result.get("artleDate")));
            article.setType(type);
            ArticleContext context = new ArticleContext();
            context.setImageRes((Map)JSON.parse(result.get("resource")));
            context.setContent(result.get("artleContext"));
            article.setContext(context);
            articleService.saveArticle(article,context);
        }

    }

    private void writeType(ArticleType type, ZipOutputStream zout, String parentName) throws Exception{
        if(type.getChildType() != null && type.getChildType().size() > 0){
            try {
                List<Article> articles = articleService.loadArticles(type);
                if(articles != null){
                    for (Article elem: articles){
                        zout.putNextEntry(new ZipEntry(parentName + "/" + elem.getTitle()));
                        String result = writeArtleJSON(elem);
                        zout.write(result.getBytes("utf8"));
                    }
                }
                for(ArticleType typeElem:type.getChildType()){
                    writeType(typeElem,zout,parentName + "/" + typeElem.getName());
                }
            }catch (Exception e){

            }
        }else{
            List<Article> articles = articleService.loadArticles(type);
            for (Article elem: articles){
                zout.putNextEntry(new ZipEntry(parentName + "/" + elem.getTitle()));
                String result = writeArtleJSON(elem);
                zout.write(result.getBytes("utf8"));
            }
        }
    }

    private String writeArtleJSON(Article article){
        ArticleContext context = articleService.loadContext(article);
        Map<String,String> output = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        output.put("artleTitle", article.getTitle());
        output.put("artleTypeName", article.getType().getName());
        output.put("artleContext",context.getContent());
        output.put("artleDate",sdf.format(article.getCreatedDate()));
        output.put("resource", JSON.toJSONString(context.getImageRes()));
        String result = JSON.toJSONString(output);
        return result;
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
