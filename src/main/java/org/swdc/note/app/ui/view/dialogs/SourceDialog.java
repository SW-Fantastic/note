package org.swdc.note.app.ui.view.dialogs;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;

@FXMLView("/view/sourceDialog.fxml")
public class SourceDialog extends AbstractFxmlView {

    @Autowired
    private UIConfig config;

    @Getter
    private Stage stage;

    @PostConstruct
    public void initUI() throws Exception {
        BorderPane pane = (BorderPane) this.getView();
        UIUtil.configTheme(pane,config);

        HBox hbox = (HBox)pane.getCenter();
        Button buttonOpen = (Button) UIUtil.findById("btnOpen",hbox.getChildren());
        buttonOpen.setFont(UIConfig.getFontIconSmall());
        buttonOpen.setText(UIConfig.getAwesomeMap().get("folder") + "");

        Platform.runLater(() -> {
            stage = new Stage();
            stage.setMaximized(false);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("创建");
            stage.initOwner(GUIState.getStage());
            Scene scene = new Scene(pane);
            stage.setScene(scene);
        });
    }

    public void show() {
        if (stage.isShowing()) {
            stage.requestFocus();
        } else {
            stage.showAndWait();
        }
    }

    public void hide() {
        if (stage.isShowing()) {
            stage.hide();
        }
    }

}
