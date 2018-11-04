package org.swdc.note.app.ui.view.dialogs;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * 分类编辑器视图
 */
@FXMLView("/view/typeDialog.fxml")
public class TypeDialog extends AbstractFxmlView {

    @Getter
    private Stage stage;

    @Autowired
    private UIConfig config;

    @Setter
    private ArticleType articleType;

    @PostConstruct
    protected void initUI() throws Exception{
        BorderPane pane = (BorderPane)getView();
        UIUtil.configTheme(pane,config);
        Platform.runLater(()->{
            stage = new Stage();
            Scene sc = new Scene(pane);
            stage.setScene(sc);
            stage.getIcons().addAll(UIConfig.getImageIcons());
            stage.initOwner(GUIState.getStage());
            stage.setResizable(false);
            stage.setTitle("分类管理");
        });
        Optional.ofNullable((Button) pane.lookup("#btnAdd")).ifPresent(btn->{
            btn.setFont(UIConfig.getFontIconSmall());
            btn.setText(String.valueOf(UIConfig.getAwesomeMap().get("plus")));
        });
        Optional.ofNullable((Button)pane.lookup("#btnDel")).ifPresent(btn->{
            btn.setFont(UIConfig.getFontIconSmall());
            btn.setText(String.valueOf(UIConfig.getAwesomeMap().get("trash")));
        });

    }

    public ArticleType getArticleType(){
        ArticleType type = this.articleType;
        this.articleType = null;
        return type;
    }

}
