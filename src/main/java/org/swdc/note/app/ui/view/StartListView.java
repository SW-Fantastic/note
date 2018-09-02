package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;

/**
 * Created by lenovo on 2018/8/18.
 */
@FXMLView(value = "/view/listView.fxml")
public class StartListView extends AbstractFxmlView{

    @Autowired
    private UIConfig config;

    @PostConstruct
    protected void initUI() throws Exception{
        BorderPane pane = (BorderPane)this.getView();
        UIUtil.configTheme(pane,config);
    }

}
