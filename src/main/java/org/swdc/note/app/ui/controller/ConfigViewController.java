package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    protected void onConfigSave() throws Exception{
        config.setTheme(combTheme.getSelectionModel().getSelectedItem());
        config.setBackground(combImg.getSelectionModel().getSelectedItem());
        DataUtil.writeConfigProp(config);
    }

}
