package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.collections.ObservableList;
import javafx.scene.Node;
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

    @Autowired
    private StartListView viewList;

    @Autowired
    private StartEditView viewEdit;

    @PostConstruct
    protected void initUI() throws Exception{
        GUIState.getStage().setMinWidth(1020);
        GUIState.getStage().setMinHeight(680);
        BorderPane pane = (BorderPane) this.getView();
        pane.setCenter(viewList.getView());
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
                    btn.selectedProperty().addListener(((observable, oldValue, newValue) -> {
                        if(newValue!=null&&newValue){
                            pane.setCenter(viewList.getView());
                        }
                    }));
                });

        Optional.ofNullable((ToggleButton) findById("write",tool.getItems()))
                .ifPresent(btn-> {
                    initToolBtn(btn,"file");
                    btn.selectedProperty().addListener(((observable, oldValue, newValue) -> {
                        if(newValue!=null&&newValue){
                            pane.setCenter(viewEdit.getView());
                        }
                    }));
                });
        Optional.ofNullable((ToggleButton) findById("config",tool.getItems()))
                .ifPresent(btn-> initToolBtn(btn,"cog"));

        Button btnSearch = (Button) getView().lookup("#search");
        btnSearch.setFont(UIConfig.getFontIcon());
        btnSearch.setText(String.valueOf(UIConfig.getAwesomeMap().get("search")));
    }

    @PostConstruct
    protected void initUIEvent(){
        toolsGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null){
                oldValue.setSelected(true);
            }
        });
        BorderPane pane = (BorderPane) this.getView();
        pane.widthProperty().addListener(num->{
            ((BorderPane) viewList.getView()).setPrefWidth(pane.getWidth() - ((BorderPane)pane.getLeft()).getPrefWidth());
            ((BorderPane) viewEdit.getView()).setPrefWidth(pane.getWidth() - ((BorderPane)pane.getLeft()).getPrefWidth());
        });
        pane.heightProperty().addListener(num->{
            ((BorderPane) viewList.getView()).setPrefHeight(pane.getHeight());
            ((BorderPane) viewEdit.getView()).setPrefHeight(pane.getHeight());
        });
    }

    private void initToolBtn(ToggleButton btn,String iconName){
        btn.setFont(UIConfig.getFontIcon());
        btn.setText(String.valueOf(UIConfig.getAwesomeMap().get(iconName)));
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
