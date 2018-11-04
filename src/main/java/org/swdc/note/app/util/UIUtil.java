package org.swdc.note.app.util;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.swdc.note.app.ui.UIConfig;

import java.io.*;
import java.util.Properties;

/**
 * UI工具类
 */
public class UIUtil {

    public static String readFile(InputStream in) throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = reader.readLine();
        String buff = line;
        while ((line = reader.readLine())!=null){
            buff = buff + line;
        }
        return buff;
    }

    public static byte[] readFile(FileInputStream in) throws Exception{
        DataInputStream din = new DataInputStream(in);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        while ((din.read(buff)>0)){
            bout.write(buff);
        }
        bout.flush();
       return bout.toByteArray();
    }

    public static void processWriteFile(File file,String content){
        try (FileOutputStream fos = new FileOutputStream(file)){
            BufferedWriter bwr = new BufferedWriter(new OutputStreamWriter(fos));
            bwr.write(content);
            bwr.flush();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static  void configTheme(Pane pane,UIConfig config) throws Exception{
        if(config.getTheme().equals("")||config.getTheme().equals("default")){
            pane.getStylesheets().add(new ClassPathResource("style/start.css").getURL().toExternalForm());
            pane.getStylesheets().add(new ClassPathResource("style/java-keywords.css").getURL().toExternalForm());
        }else{
            pane.getStylesheets().add("file:configs/theme/"+config.getTheme()+"/"+config.getTheme()+".css");
            pane.getStylesheets().add("file:configs/theme/"+config.getTheme()+"/"+config.getTheme()+".keyword.css");
        }
    }

    public static boolean isClassical(){
        try{
            Properties props = new Properties();
            props.load(new FileInputStream("configs/config.properties"));
            return props.get("app.mode").equals("classical");
        }catch (Exception e){
            return false;
        }
    }

    public static Node findById(String id, ObservableList<Node> list){
        for (Node node:list) {
            if(node.getId().equals(id)){
                return node;
            }
        }
        return null;
    }

}
