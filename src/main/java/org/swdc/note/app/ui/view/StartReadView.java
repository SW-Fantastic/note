package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;

/**
 * 阅览页面
 */
@FXMLView("/view/readView.fxml")
public class StartReadView extends AbstractFxmlView{

    @Autowired
    private UIConfig config;

    @Getter
    private WebView webView;

    @PostConstruct
    protected void initUI() throws Exception {
        BorderPane root = (BorderPane) getView();
        UIUtil.configTheme(root,config);
        Platform.runLater(()->{
            WebView view  = new WebView();
            root.setCenter(view);
            this.webView = view;
        });
    }

}
