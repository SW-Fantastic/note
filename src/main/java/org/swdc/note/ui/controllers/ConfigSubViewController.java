package org.swdc.note.ui.controllers;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import org.swdc.fx.view.ViewController;
import org.swdc.note.config.AppConfig;
import org.swdc.note.config.RenderConfig;
import org.swdc.note.ui.events.ConfigRefreshEvent;
import org.swdc.note.ui.view.ConfigSubView;
import org.swdc.note.ui.view.UIUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigSubViewController extends ViewController<ConfigSubView> {

    @Inject
    private AppConfig config = null;

    @Inject
    private RenderConfig renderConfig = null;

    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void saveProperties() {
        try {
            ConfigSubView subView = getView();
            config.save();
            renderConfig.save();
            subView.emit(new ConfigRefreshEvent(config));
            subView.emit(new ConfigRefreshEvent(renderConfig));
            UIUtils.notification("设置保存成功。");
        } catch (Exception e){
            UIUtils.notification("设置保存失败。");
        }
    }

}
