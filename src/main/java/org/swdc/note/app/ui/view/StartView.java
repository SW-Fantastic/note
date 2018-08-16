package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.swdc.note.app.ui.UIConfig;

import javax.annotation.PostConstruct;

@FXMLView(value = "/view/start.fxml")
public class StartView extends AbstractFxmlView {

    @Autowired
    private UIConfig config;

    private ToggleGroup toolsGroup = new ToggleGroup();

    @PostConstruct
    protected void initUI() throws Exception{
        BorderPane pane = (BorderPane) this.getView();
        pane.setStyle(pane.getStyle()+";-fx-background-image: url("+UIConfig.getConfigLocation()+config.getBackground()+");");

        if(config.getTheme().equals("")||config.getTheme().equals("def")){
            pane.getStylesheets().add(new ClassPathResource("style/start.css").getURL().toExternalForm());
        }else{
            pane.getStylesheets().add("file:configs/theme/"+config.getTheme()+"/"+config.getTheme()+".css");
        }

        BorderPane paneLeft = (BorderPane)pane.getLeft();
        ToolBar tool = (ToolBar) paneLeft.getRight();
        tool.getItems().forEach(toolItem->{

            ToggleButton btn = (ToggleButton)toolItem;
            btn.setFont(UIConfig.getGLYPH_FONTAWESOME());

            if(btn.getText().equals("列表")){
                btn.setText(String.valueOf(UIConfig.getGLYPH_MAP().get("list")));
                btn.setSelected(true);
            }else if (btn.getText().equals("写作")){
                btn.setText(String.valueOf(UIConfig.getGLYPH_MAP().get("file")));
            }else if(btn.getText().equals("配置")){
                btn.setText(String.valueOf(UIConfig.getGLYPH_MAP().get("cog")));
            }
            toolsGroup.getToggles().add(btn);
        });
    }

}
