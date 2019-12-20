package org.swdc.note.app.util;

import de.felixroske.jfxsupport.GUIState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;
import org.springframework.core.io.ClassPathResource;
import org.swdc.note.app.configs.ConfigProp;
import org.swdc.note.app.configs.ConfigProperty;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.PropertyEditors;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Properties;

/**
 * UI工具类
 */
public class UIUtil {

    private static boolean classical;

    private static boolean useFloat;

    static {
        try{
            Properties props = new Properties();
            props.load(new FileInputStream("configs/config.properties"));
            classical = props.get("app.mode").equals("classical");
            useFloat = props.get("app.run-in-background").toString().toLowerCase().equals("true");
            String back = props.getProperty("app.background");
            File file = new File("./configs/res/" + back);
            if (!file.exists()){
                UIUtil.writeFile(file,new FileInputStream("./configs/theme/"+props.getProperty("app.theme")+"/"+back));
            }
        }catch (Exception e){
            classical = false;
        }
    }

    public static String readFileAsText(InputStream in) throws Exception{
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

    public static void writeFile(File file,InputStream in){
        try (FileOutputStream fos = new FileOutputStream(file)){
            DataOutputStream dos = new DataOutputStream(fos);
            DataInputStream din = new DataInputStream(in);
            byte[] buf = new byte[1024];
            int length;
            while ((length = din.read(buf)) > 0){
                dos.write(buf,0,length);
            }
            dos.flush();
            dos.close();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void processWriteFile(File file,String content){
        try (FileOutputStream fos = new FileOutputStream(file)){
            BufferedWriter bwr = new BufferedWriter(new OutputStreamWriter(fos));
            bwr.write(content);
            bwr.flush();
            bwr.close();
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
            pane.getStylesheets().add("file:configs/theme/"+config.getTheme()+"/"+config.getTheme()+".keywords.css");
        }
    }

    public static boolean isClassical(){
        return classical;
    }

    public static boolean isUseFloat(){
        return useFloat;
    }

    public static Node findById(String id, ObservableList<Node> list){
        for (Node node:list) {
            if(id.equals(node.getId())){
                return node;
            }
        }
        return null;
    }

    public static Optional<ButtonType> showAlertDialog(String content, String title, Alert.AlertType type, UIConfig config) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.initOwner(GUIState.getStage());
        try {
            UIUtil.configTheme(alert.getDialogPane(), config);
            return alert.showAndWait();
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Optional<ButtonType> showAlertWithOwner(String content, String title, Alert.AlertType type, Window owner, UIConfig config) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.initOwner(owner);
        try {
            UIUtil.configTheme(alert.getDialogPane(), config);
            return alert.showAndWait();
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static double getScreenX(Node node) {
        return  node.localToScreen(0,0).getX();
    }

    public static double getScreenY(Node node) {
        return node.localToScreen(0,0).getY();
    }

    public static ObservableList<PropertySheet.Item> getProperties(Object object) throws Exception {
        ObservableList<PropertySheet.Item> list = FXCollections.observableArrayList();
        Field[] fields = object.getClass().getDeclaredFields();
        for(Field field: fields) {
            if (field.getAnnotation(ConfigProp.class) == null){
                continue;
            }
            ConfigProp propDefinition = field.getAnnotation(ConfigProp.class);
            ConfigProperty property = new ConfigProperty(object,new PropertyDescriptor(field.getName(),object.getClass()),propDefinition);
            list.add(property);
        }
        return list;
    }

    public static PropertyEditor<?> getEditor(PropertySheet.Item prop, UIConfig config) {
        if (!(prop instanceof ConfigProperty)) {
            return null;
        }
        ConfigProperty property = (ConfigProperty) prop;
        ConfigProp propData = property.getPropData();
        switch (propData.type()) {
            case FILE_SELECT_IMPORTABLE:
                return PropertyEditors.createFileImportableEditor(property, config);
            case FOLDER_SELECT_IMPORTABLE:
                return PropertyEditors.createFolderImportableEditor(property,config);
            case CHECK:
                return PropertyEditors.createCheckedEditor(property);
            case COLOR:
                return PropertyEditors.createColorEditor(property);
            case NUMBER_SELECTABLE:
                return PropertyEditors.createNumberRangeEditor(property);
            case NUMBER:
                return PropertyEditors.createNumberEditor(property);
        }
        return null;
    }

}
