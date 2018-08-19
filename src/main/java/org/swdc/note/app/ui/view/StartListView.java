package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.swdc.note.app.ui.UIConfig;

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
        if(config.getTheme().equals("")||config.getTheme().equals("def")){
            pane.getStylesheets().add(new ClassPathResource("style/start.css").getURL().toExternalForm());
        }else{
            pane.getStylesheets().add("file:configs/theme/"+config.getTheme()+"/"+config.getTheme()+".css");
        }
    }

}
