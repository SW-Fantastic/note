package org.swdc.note.app.ui.view.classes;

import static org.swdc.note.app.util.UIUtil.findById;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.application.Platform;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import org.springframework.context.annotation.Conditional;

import javax.annotation.PostConstruct;

/**
 * Created by lenovo on 2018/10/27.
 */
@Conditional(NotViewNormalCondition.class)
@FXMLView("/view/classes/legListView.fxml")
public class ClListView extends AbstractFxmlView {

    private WebView preview;

    @PostConstruct
    public void initUI(){
        BorderPane pane = (BorderPane)this.getView();
        BorderPane content = (BorderPane) findById ("content",((SplitPane)pane.getCenter()).getItems());
        Platform.runLater(()->{
            WebView contentView = new WebView();
            contentView.setId("webView");
            content.setCenter(contentView);
            this.preview = contentView;
        });
    }

    public  void setContent(String content){
        this.preview.getEngine().loadContent(content);
    }

}
