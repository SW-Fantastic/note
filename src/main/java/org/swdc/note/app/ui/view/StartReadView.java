package org.swdc.note.app.ui.view;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserContextParams;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
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

    @Autowired
    private BrowserContext browserContext;

    @Getter
    private BrowserView webView;

    @Getter
    private Stage stage;

    @PostConstruct
    protected void initUI() throws Exception {
        BorderPane root = (BorderPane) getView();
        UIUtil.configTheme(root,config);
        Platform.runLater(()->{
            BrowserView view = new BrowserView(new Browser(browserContext));
            root.setCenter(view);
            this.webView = view;
        });
        ToolBar toolBar = (ToolBar)root.getTop();
        Button btnExport = (Button) findById("export",toolBar.getItems());
        btnExport.setFont(UIConfig.getFontIconSmall());
        btnExport.setText(String.valueOf(UIConfig.getAwesomeMap().get("sign_out")));
        Button btnOpen = (Button)findById("open",toolBar.getItems());
        btnOpen.setFont(UIConfig.getFontIconSmall());
        btnOpen.setText(String.valueOf(UIConfig.getAwesomeMap().get("folder_open")));
        Button btnImport = (Button)findById("import",toolBar.getItems());
        btnImport.setVisible(false);
        btnImport.setFont(UIConfig.getFontIconSmall());
        btnImport.setText(String.valueOf(UIConfig.getAwesomeMap().get("sign_in")));
        if(UIUtil.isClassical()){
            Platform.runLater(()->{
                stage = new Stage();
                stage.initOwner(GUIState.getStage());
                Scene scene = new Scene(this.getView());
                String res = new StringBuilder(UIConfig.getConfigLocation()).append("res/").append(config.getBackground()).toString();
                this.getView().setStyle(this.getView().getStyle()+";-fx-background-image: url("+res+");");
                stage.setScene(scene);
                stage.setMinWidth(800);
                stage.setMinHeight(600);
                stage.getIcons().addAll(UIConfig.getImageIcons());
                stage.setTitle("阅览");
            });
        }
    }

    private Node findById(String id, ObservableList<Node> list){
        for (Node node:list) {
            if(node.getId().equals(id)){
                return node;
            }
        }
        return null;
    }

}
