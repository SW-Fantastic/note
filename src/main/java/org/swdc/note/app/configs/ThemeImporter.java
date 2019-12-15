package org.swdc.note.app.configs;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import org.swdc.note.app.util.UIUtil;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ThemeImporter implements Importer {
    @Override
    public void install(File file) throws Exception {
        if (file == null){
            return;
        }
        ZipFile zip = new ZipFile(file);
        ZipEntry entry = zip.getEntry("config.properties");
        if (entry == null){
            throw new Exception("不是一个有效的主题文件");
        }
        Properties properties = new Properties();
        properties.load(zip.getInputStream(entry));
        String name = properties.get("name").toString();
        File targetFolder = new File("./configs/theme/"+name);
        if (targetFolder.exists()){
            throw new Exception("主题名称冲突，无法安装。");
        }
        if(!targetFolder.mkdir()){
            throw new Exception("无法创建文件夹，安装失败。");
        }
        Enumeration<? extends ZipEntry> enumerate = zip.entries();
        while (enumerate.hasMoreElements()){
            ZipEntry ent = enumerate.nextElement();
            InputStream in = zip.getInputStream(ent);
            UIUtil.writeFile(new File("./configs/theme/" + name + "/" + ent.getName()),in);
        }
        if (properties.get("app.background") != null ){
            entry = zip.getEntry(properties.get("app.background").toString());
            UIUtil.writeFile(new File("./configs/res/" + entry.getName()),zip.getInputStream(entry));
        }
    }

    @Override
    public String supportName() {
        return "主题文件";
    }

    @Override
    public String[] extensions() {
        return new String[]{"zip", "xstyle"};
    }
}
