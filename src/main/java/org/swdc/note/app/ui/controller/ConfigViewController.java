package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.DataUtil;

import java.net.URL;
import java.util.ResourceBundle;

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
    private CheckBox cbxFloat;

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
        cbxFloat.setSelected(config.getUseFloat());
    }

    @FXML
    protected void onConfigSave() throws Exception{
        config.setTheme(combTheme.getSelectionModel().getSelectedItem());
        config.setBackground(combImg.getSelectionModel().getSelectedItem());
        config.setMode(radioGp.getSelectedToggle().getUserData().toString());
        config.setUseFloat(cbxFloat.isSelected());
        DataUtil.writeConfigProp(config);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("设置已经更改，下次启动时会生效。");
        alert.setHeaderText(null);
        alert.setTitle("提示");
        alert.showAndWait();
    }

}
