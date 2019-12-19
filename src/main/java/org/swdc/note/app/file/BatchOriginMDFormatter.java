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
                    typePathArticleMap.get(parent).add(ent.getName());
                }
            }

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

    public Object resolveZipEntry(ZipEntry ent, ArticleType type, ZipFile zipFile) throws Exception{
        if(ent.isDirectory() && !ent.getName().equals("source") && !ent.getName().equals(type.getName())){
            ArticleType subType = new ArticleType();
            subType.setName(ent.getName().split("[/\\\\]")[ent.getName().split("[/\\\\]").length - 1]);
            subType.setArticles(new ArrayList<>());
            if (type != null) {
                subType.setParentType(type);
                type.getChildType().add(subType);
            }
            return subType;
        }else{
            if (type == null) {
                throw new RuntimeException("format is not correct");
            }
            InputStream in = zipFile.getInputStream(ent);
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


}
