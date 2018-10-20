package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.swdc.note.app.NoteApplication;
import org.swdc.note.app.event.ResetEvent;
import org.swdc.note.app.event.ViewChangeEvent;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;
import javax.swing.*;
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

    /**
     * 处理边栏的界面切换按钮
     */
    private class ViewToolButtonHandler implements ChangeListener<Boolean>{

        /**
         * 切换的目标视图
         */
        private Node targetView;
        /**
         * 当前切换按钮
         */
        private ToggleButton obsButton;

        protected ViewToolButtonHandler(ToggleButton btn,Node connectedView){
            this.targetView = connectedView;
            this.obsButton = btn;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if(newValue!=null&&newValue){
                BorderPane pane = (BorderPane) StartView.this.getView();
                if(targetView!=viewEdit.getView() && pane.getCenter().equals(viewEdit.getView()) && !viewEdit.getDocument().trim().equals("")){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setContentText("你正在离开编辑页面，这样会失去正在编辑的内容，确定要这样吗？");
                    alert.setTitle("提示");
                    alert.initOwner(GUIState.getStage());
                    alert.setHeaderText(null);
                    Optional<ButtonType> result = alert.showAndWait();
                    result.ifPresent(btnSel->{
                        if(btnSel.equals(ButtonType.OK)) {
                            config.publishEvent(new ResetEvent(""));
                            pane.setCenter(this.targetView);
                            toolsGroup.setUserData(obsButton);
                        }else if (toolsGroup.getUserData()!=null){
                            toolsGroup.selectToggle((ToggleButton)toolsGroup.getUserData());
                        }
                    });
                }else{
                    pane.setCenter(targetView);
                    toolsGroup.setUserData(obsButton);
                }
            }
        }
    }

    @PostConstruct
    protected void initUI() throws Exception{
        GUIState.getStage().setTitle("幻想笔记");
        GUIState.getStage().setMinWidth(1020);
        GUIState.getStage().setMinHeight(680);
        BorderPane pane = (BorderPane) this.getView();
        pane.setCenter(viewList.getView());
        String res = new StringBuilder(UIConfig.getConfigLocation()).append("res/").append(config.getBackground()).toString();
        pane.setStyle(pane.getStyle()+";-fx-background-image: url("+res+");");

        UIUtil.configTheme(pane,config);

        ToolBar tool = (ToolBar) getView().lookup(".tool");

        // 使用font-awsome的字体图标
        Optional.ofNullable((ToggleButton) findById("list",tool.getItems()))
                .ifPresent(btn-> {
                    initToolBtn(btn,"list");
                    btn.setSelected(true);
                    toolsGroup.setUserData(btn);
                    btn.selectedProperty().addListener(new ViewToolButtonHandler(btn,viewList.getView()));
                });

        Optional.ofNullable((ToggleButton) findById("write",tool.getItems()))
                .ifPresent(btn-> {
                    initToolBtn(btn,"file");
                    btn.selectedProperty().addListener(new ViewToolButtonHandler(btn,viewEdit.getView()));
                });

        Optional.ofNullable((ToggleButton)findById("read",tool.getItems()))
                .ifPresent(btn->{
                    initToolBtn(btn,"book");
                    btn.selectedProperty().addListener(new ViewToolButtonHandler(btn,viewRead.getView()));
                });

        Optional.ofNullable((ToggleButton) findById("config",tool.getItems()))
                .ifPresent(btn->{
                    initToolBtn(btn,"cog");
                    btn.selectedProperty().addListener(new ViewToolButtonHandler(btn,viewConfig.getView()));
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
        if(pane == viewEdit.getView()){
            System.out.println("prev - editing");
        }
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
