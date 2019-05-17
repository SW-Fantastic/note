package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.event.ReLaunchEvent;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.DataUtil;
import org.swdc.note.app.util.UIUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 设置界面的控制器
 */
@FXMLController
public class ConfigViewController implements Initializable {

    @Autowired
    private UIConfig config;

    @FXML
    private ComboBox<String> combTheme;

    @FXML
    private ComboBox<String> combImg;

    @FXML
    private RadioButton radioUIClassical;

    @FXML
    private RadioButton radioUISimple;

    @FXML
    private CheckBox cbxBackground;

    @FXML
    private CheckBox cbxTrayWinLike;

    @FXML
    private Slider sldEditorFont;

    private ToggleGroup radioGp = new ToggleGroup();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        radioUIClassical.setUserData("classical");
        radioUISimple.setUserData("simple");
        radioGp.getToggles().add(radioUIClassical);
        radioGp.getToggles().add(radioUISimple);
        if(config.getMode().equals("classical")){
            radioUIClassical.setSelected(true);
        }else{
            radioUISimple.setSelected(true);
        }
        cbxBackground.setSelected(config.getRunInBackground());
        cbxTrayWinLike.setSelected(config.getWindStyledPopup());
        String osName = System.getProperty("os.name");
        if (osName != null && osName.toLowerCase().startsWith("win")) {
            cbxTrayWinLike.setSelected(true);
            cbxTrayWinLike.setDisable(true);
        }
    }

    @FXML
    protected void onConfigSave() throws Exception{
        config.setTheme(combTheme.getSelectionModel().getSelectedItem());
        config.setBackground(combImg.getSelectionModel().getSelectedItem());
        config.setMode(radioGp.getSelectedToggle().getUserData().toString());
        config.setRunInBackground(cbxBackground.isSelected());
        config.setWindStyledPopup(cbxTrayWinLike.isSelected());
        config.setEditorFontSize(Double.valueOf(sldEditorFont.getValue()).intValue());
        DataUtil.writeConfigProp(config);
        Optional<ButtonType> result = UIUtil.showAlertDialog("重新启动应用以使配置生效吗？", "提示", Alert.AlertType.CONFIRMATION,config);
        result.ifPresent(buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                config.publishEvent(new ReLaunchEvent(""));
            }
        });
    }

    @FXML
    protected void onInstallTheme(){
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("zip格式主题","*.theme.zip"));
        File file = chooser.showOpenDialog(null);
        if (file == null){
            return;
        }
        try {
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
            combTheme.getItems().clear();
            File[] themes = new File("configs/theme").listFiles();
            List<String> lstTheme = Arrays.asList(themes).stream().filter(item->item.isDirectory())
                    .map(item->item.getName())
                    .collect(Collectors.toList());
            combTheme.getItems().addAll(lstTheme);
            combTheme.getSelectionModel().select(config.getTheme());
            combImg.getItems().clear();
            File[] imgs = new File("configs/res").listFiles();
            List<String> lstImgs = Arrays.asList(imgs).stream().map(item->item.getName())
                    .collect(Collectors.toList());
            combImg.getItems().addAll(lstImgs);
            combImg.getSelectionModel().select(config.getBackground());
            UIUtil.showAlertDialog("安装成功，请检查主题组合框。", "提示", Alert.AlertType.INFORMATION,config);
        }catch (Exception e) {
            UIUtil.showAlertDialog("无法安装这个主题文件 " + e.getMessage() + e.getCause(),
                    "错误", Alert.AlertType.ERROR, config);
        }
    }

    @FXML
    protected void deleteBackground(){
        UIUtil.showAlertDialog("你确实要删除《"+
                combImg.getSelectionModel().getSelectedItem()+"》吗？",
                "提示", Alert.AlertType.CONFIRMATION, config).ifPresent(btnType->{
            if(btnType.equals(ButtonType.OK)){
                File file = new File("./configs/res/"+combImg.getSelectionModel().getSelectedItem());
                if (file.exists()){
                    if(!file.delete()){
                        file.deleteOnExit();
                    }
                    File[] imgs = new File("configs/res").listFiles();
                    List<String> lstImgs = Arrays.asList(imgs).stream().map(item->item.getName())
                            .collect(Collectors.toList());
                    combImg.getItems().clear();
                    combImg.getItems().addAll(lstImgs);
                    combImg.getSelectionModel().select(config.getBackground());
                }
            }
        });
    }

    @FXML
    protected void importBackground(){
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("各种图片","*.png","*.jpg","*.jpeg","*.gif"));
        File file = chooser.showOpenDialog(null);
        if (file == null){
            return;
        }
        try {
            UIUtil.writeFile(new File("./configs/res"+file.getName()),new FileInputStream(file));
        }catch (Exception e){
            UIUtil.showAlertDialog("无法导入文件 " + e.getMessage() + e.getCause(), "异常", Alert.AlertType.ERROR, config);
        }
    }

}
