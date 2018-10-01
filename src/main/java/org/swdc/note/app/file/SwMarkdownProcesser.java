package org.swdc.note.app.file;

import com.alibaba.fastjson.JSON;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.entity.ArtleContext;
import org.swdc.note.app.entity.ArtleType;
import org.swdc.note.app.service.ArtleService;
import org.swdc.note.app.util.UIUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  用来读写mdxn（mdzz）格式的markdown数据，
 *  此数据是json格式的数据，由本工程创建使用。
 */
@Component
public class SwMarkdownProcesser extends FileFormater {

    @Autowired
    private ArtleService artleService;

    @Override
    public String getFormatName() {
        return "笔记默认格式（*.mdxn）（*.mdzz)";
    }

    @Override
    public <T> T processRead(File target) {
        String name = target.getAbsolutePath();
        String[] extName = name.split("[.]");
        if(extName[extName.length - 1].equals("mdxn")){
            try {
                String source = UIUtil.readFile((InputStream)new FileInputStream(target));
                Map<String,String> result = (Map) JSON.parse(source);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Artle artle = new Artle();
                artle.setTitle(result.get("artleTitle"));
                artle.setCreatedDate(sdf.parse(result.get("artleDate")));
                ArtleContext context = new ArtleContext();
                context.setImageRes((Map)JSON.parse(result.get("resource")));
                context.setContent(result.get("artleContext"));
                artle.setContext(context);
                return (T)artle;
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
        if(targetObj instanceof Artle){
            Artle artleTarget = (Artle)targetObj;
            ArtleContext context = artleService.loadContext(artleTarget);
            Map<String,String> output = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            output.put("artleTitle",artleTarget.getTitle());
            output.put("artleTypeName",artleTarget.getType().getName());
            output.put("artleContext",context.getContent());
            output.put("artleDate",sdf.format(artleTarget.getCreatedDate()));
            output.put("resource", JSON.toJSONString(context.getImageRes()));
            String result = JSON.toJSONString(output);
            if (!nameExt[nameExt.length - 1].equals("mdxn")){
                target = new File(target.getAbsolutePath() + ".mdxn");
            }
            UIUtil.processWriteFile(target,result);
        }else if(targetObj instanceof ArtleType){

        }
    }

    @Override
    public List<FileChooser.ExtensionFilter> getFilters() {
        return Arrays.asList(new FileChooser.ExtensionFilter("文档原始数据","*.mdxn","*.mdzz"));
    }
}
