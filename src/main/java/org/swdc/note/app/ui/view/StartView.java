package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.swdc.note.app.ui.UIConfig;

import javax.annotation.PostConstruct;
import java.util.Optional;

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

        ToolBar tool = (ToolBar) getView().lookup(".tool");

        // 使用font-awsome的字体图标
        Optional.ofNullable((ToggleButton) findById("list",tool.getItems()))
                .ifPresent(btn-> {
                    initToolBtn(btn,"list");
                    btn.setSelected(true);
                });

        Optional.ofNullable((ToggleButton) findById("write",tool.getItems()))
                .ifPresent(btn-> initToolBtn(btn,"file"));
        Optional.ofNullable((ToggleButton) findById("config",tool.getItems()))
                .ifPresent(btn-> initToolBtn(btn,"cog"));

        Button btnSearch = (Button) getView().lookup("#search");
        btnSearch.setFont(UIConfig.getGLYPH_FONTAWESOME());
        btnSearch.setText(String.valueOf(UIConfig.getGLYPH_MAP().get("search")));
    }

    @PostConstruct
    protected void initUIEvent(){
        toolsGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null){
                oldValue.setSelected(true);
            }
        });
    }

    private void initToolBtn(ToggleButton btn,String iconName){
        btn.setFont(UIConfig.getGLYPH_FONTAWESOME());
        btn.setText(String.valueOf(UIConfig.getGLYPH_MAP().get(iconName)));
        toolsGroup.getToggles().add(btn);
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
