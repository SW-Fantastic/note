package org.swdc.note.app.file;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleContext;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.service.ArticleService;
import org.swdc.note.app.util.UIUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Component
public class BatchOriginMDFormatter extends AbstractFormatter<ArticleType> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private ArticleService articleService;

    @Override
    public boolean supportObject(Class type) {
        return type == ArticleType.class;
    }

    @Override
    public ArticleType readDocument(File file) {

        Map<String, List<String>> typePathArticleMap = new HashMap<>();
        Map<String, ArticleType> typePathTypeMap = new HashMap<>();

        try (ZipFile zipFile = new ZipFile(file)){
            Enumeration<? extends ZipEntry> zipEnum = zipFile.entries();
            while (zipEnum.hasMoreElements()){
                ZipEntry ent = zipEnum.nextElement();
                if (ent.isDirectory()) {
                    if (typePathArticleMap.containsKey(ent.getName())) {
                        continue;
                    }
                    typePathArticleMap.put(ent.getName(), new ArrayList<>());
                } else {
                    String[] nameItem = ent.getName().split("/");
                    String parent = ent.getName().substring(0, ent.getName().indexOf(nameItem[nameItem.length - 1]));
                    List<String> contents = typePathArticleMap.get(parent);
                    if (contents == null) {
                        contents = new ArrayList<>();
                        typePathArticleMap.put(parent,contents);
                    }
                    contents.add(ent.getName());
                }
            }

            for (String key: typePathArticleMap.keySet()){
                ArticleType type = new ArticleType();
                List<Article> articles = new ArrayList<>();

                String[] names = key.split("/");
                String name = names[names.length - 1];
                type.setName(name);

                List<String> artFiles = typePathArticleMap.get(key);
                for (String item : artFiles) {
                    ZipEntry entry = zipFile.getEntry(item);
                    Article article = resolveZipEntry(entry,type,zipFile);
                    if (article == null) {
                        continue;
                    }
                    articles.add(article);
                }

                type.setArticles(articles);
                type.setChildType(new HashSet<>());
                typePathTypeMap.put(key,type);
            }

            for (String key : typePathTypeMap.keySet()) {
                String[] keyItems = key.split("/");
                String parent = key.substring(0, key.indexOf(keyItems[keyItems.length - 1]));
                ArticleType type = typePathTypeMap.get(parent);
                ArticleType current = typePathTypeMap.get(key);
                if (current.getParentType() == null && type != null) {
                    current.setParentType(type);
                    type.getChildType().add(current);
                }
            }

            for (ArticleType type : typePathTypeMap.values()) {
                if (type.getParentType() == null) {
                    return type;
                }
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void writeDocument(File file, ArticleType type) {
        String[] nameExt = file.getName().split("[.]");
        if (!nameExt[nameExt.length - 1].equals("mdzz")){
            file = new File(file.getAbsolutePath() + ".mdzz");
        }
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))){
            writeType(type,zos,"source/"+type.getName());
            zos.flush();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getFormatName() {
        return "markdown数据集";
    }

    @Override
    public String getFormatExtension() {
        return "mdzz";
    }

    @Override
    public boolean isBatch() {
        return true;
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

    public Article resolveZipEntry(ZipEntry ent, ArticleType type, ZipFile zipFile) throws Exception{
        if(ent.isDirectory()){
            return null;
        }
        if (type == null) {
            throw new RuntimeException("format is not correct");
        }
        try(InputStream in = zipFile.getInputStream(ent)) {
            Article article = new Article();
            String source = UIUtil.readFileAsText(in);
            Map<String,String> result = (Map)JSON.parse(source);
            article.setTitle(result.get("artleTitle"));
            article.setCreatedDate(dateFormat.parse(result.get("artleDate")));
            article.setType(type);
            ArticleContext context = new ArticleContext();
            context.setImageRes((Map)JSON.parse(result.get("resource")));
            context.setContent(result.get("artleContext"));
            article.setContext(context);
            in.close();
            return article;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
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


}
