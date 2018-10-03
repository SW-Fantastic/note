package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
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
