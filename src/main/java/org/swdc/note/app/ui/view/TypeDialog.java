package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;

/**
 * Created by lenovo on 2018/9/3.
 */
@FXMLView("/view/typeDialog.fxml")
public class TypeDialog extends AbstractFxmlView {

    @Getter
    private Stage stage;

    @Autowired
    private UIConfig config;

    @PostConstruct
    protected void initUI() throws Exception{
        BorderPane pane = (BorderPane)getView();
        UIUtil.configTheme(pane,config);
        Platform.runLater(()->{
            stage = new Stage();
            Scene sc = new Scene(pane);
            stage.setScene(sc);
            stage.initOwner(GUIState.getStage());
        });
    }

}
