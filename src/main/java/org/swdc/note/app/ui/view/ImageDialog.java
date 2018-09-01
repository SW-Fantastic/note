package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 添加图片的窗口
 */
@FXMLView(value = "/view/InsertImgView.fxml")
public class ImageDialog extends AbstractFxmlView {

    @Autowired
    private UIConfig config;

    private Stage stage;

    @Getter
    @Setter
    private String selectedImage;

    @Getter
    private Map<String,String> images = new HashMap<>();

    @PostConstruct
    protected void initUI() throws Exception {
        BorderPane pane = (BorderPane) this.getView();
        UIUtil.configTheme(pane,config);
        Platform.runLater(()->{
            Scene sc = new Scene(pane);
            stage = new Stage();
            stage.setScene(sc);
            stage.setResizable(false);
            stage.setTitle("添加图片");
            stage.initOwner(GUIState.getStage());
        });
    }

    public Stage getStage(){
        return stage;
    }

}
