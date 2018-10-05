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
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.swdc.note.app.NoteApplication;
import org.swdc.note.app.event.ViewChangeEvent;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

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

    @Autowired
    private StartReadView viewRead;

    @Autowired
    private StartConfigView viewConfig;

    @PostConstruct
    protected void initUI() throws Exception{
        GUIState.getStage().setTitle("幻想笔记");
        GUIState.getStage().setMinWidth(1020);
        GUIState.getStage().setMinHeight(680);
        BorderPane pane = (BorderPane) this.getView();
        pane.setCenter(viewList.getView());
        pane.setStyle(pane.getStyle()+";-fx-background-image: url("+UIConfig.getConfigLocation()+"res/"+config.getBackground()+");");

        UIUtil.configTheme(pane,config);

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

        Optional.ofNullable((ToggleButton)findById("read",tool.getItems()))
                .ifPresent(btn->{
                    initToolBtn(btn,"book");
                    btn.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        if(newValue!=null && newValue){
                            pane.setCenter(viewRead.getView());
                        }
                    });
                });

        Optional.ofNullable((ToggleButton) findById("config",tool.getItems()))
                .ifPresent(btn->{
                    initToolBtn(btn,"cog");
                    btn.selectedProperty().addListener(((observable, oldValue, newValue) -> {
                        if(newValue!=null && newValue){
                            pane.setCenter(viewConfig.getView());
                        }
                    }));
                });

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
            ((BorderPane) viewRead.getView()).setPrefWidth(pane.getWidth() - ((BorderPane)pane.getLeft()).getPrefWidth());
            ((BorderPane) viewConfig.getView()).setPrefWidth(pane.getWidth() - ((BorderPane)pane.getLeft()).getPrefWidth());
        });
        pane.heightProperty().addListener(num->{
            ((BorderPane) viewList.getView()).setPrefHeight(pane.getHeight());
            ((BorderPane) viewEdit.getView()).setPrefHeight(pane.getHeight());
            ((BorderPane) viewRead.getView()).setPrefHeight(pane.getHeight());
            ((BorderPane) viewConfig.getView()).setPrefHeight(pane.getHeight());
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

    /**
     * 界面需要发生变化
     * @param e
     */
    @EventListener
    public void onViewChange(ViewChangeEvent e){
        BorderPane pane = (BorderPane) this.getView();
        ToolBar tool = (ToolBar) getView().lookup(".tool");
        if(e.getViewName().equals("EditView")){
            Optional.ofNullable((ToggleButton) findById("write",tool.getItems()))
                    .ifPresent(btn-> {
                        toolsGroup.selectToggle(btn);
                        pane.setCenter(viewEdit.getView());
                    });
        }else if(e.getViewName().equals("ListView")){
            Optional.ofNullable((ToggleButton) findById("list",tool.getItems()))
                    .ifPresent(btn-> {
                        toolsGroup.selectToggle(btn);
                        pane.setCenter(viewList.getView());
                    });
        }else if(e.getViewName().equals("ReadView")){
            Optional.ofNullable((ToggleButton)findById("read",tool.getItems()))
                    .ifPresent(btn->{
                        toolsGroup.selectToggle(btn);
                        pane.setCenter(viewRead.getView());
                    });
        }

    }

}
