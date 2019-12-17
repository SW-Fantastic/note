package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.event.ReLaunchEvent;
import org.swdc.note.app.event.RefreshEvent;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.MessageView;
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

    @Autowired
    private MessageView messageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    protected void onConfigSave() throws Exception{
        DataUtil.saveConfigFile(config);
        UIUtil.showAlertDialog("主题设置需要重新启动应用才会生效，如果你修改了主题，那么请重启，否则可以忽略此提示。是否要重启应用？"
                ,"提示", Alert.AlertType.CONFIRMATION,config).ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                config.publishEvent(new ReLaunchEvent(""));
            } else {
                config.publishEvent(new RefreshEvent(UIConfig.class));
                messageView.setMessage("配置已经保存。");
                Notifications.create()
                        .hideCloseButton()
                        .graphic(messageView.getView())
                        .position(Pos.CENTER)
                        .owner(GUIState.getStage())
                        .hideAfter(new Duration(1200))
                        .show();
            }
        });
    }

}
