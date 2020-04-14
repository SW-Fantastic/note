package org.swdc.note.ui.controllers;

import javafx.fxml.FXML;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.note.config.AppConfig;
import org.swdc.note.config.RenderConfig;
import org.swdc.note.ui.view.UIUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigSubViewController extends FXController {

    @Aware
    private AppConfig config = null;

    @Aware
    private RenderConfig renderConfig = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void saveProperties() {
        try {
            config.saveProperties();
            renderConfig.saveProperties();
            UIUtils.notification("设置保存成功。", this.getView());
        } catch (Exception e){
            UIUtils.notification("设置保存失败。", this.getView());
        }
    }

}
