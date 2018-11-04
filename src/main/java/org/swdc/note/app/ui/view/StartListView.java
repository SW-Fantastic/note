package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;

/**
 * 文档列表视图
 */
@FXMLView(value = "/view/listView.fxml")
public class StartListView extends AbstractFxmlView{

    @Autowired
    private UIConfig config;

    @PostConstruct
    protected void initUI() throws Exception{
        BorderPane pane = (BorderPane)this.getView();
        UIUtil.configTheme(pane,config);
        ListView<Article> listView = (ListView)getView().lookup("#list");
        pane.widthProperty().addListener((observable ->listView.setPrefWidth(pane.getWidth() - 32)));
    }

}
