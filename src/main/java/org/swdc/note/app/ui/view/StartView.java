package org.swdc.note.app.ui.view;

import static org.swdc.note.app.util.UIUtil.findById;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.event.ResetEvent;
import org.swdc.note.app.event.ViewChangeEvent;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.classes.NotViewClassicalCondition;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Conditional(NotViewClassicalCondition.class)
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
     * 左侧类型树
     */
    private VBox typeTreePanel;

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

        ViewToolButtonHandler(ToggleButton btn,Node connectedView){
            this.targetView = connectedView;
            this.obsButton = btn;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if(newValue!=null&&newValue){
                BorderPane pane = (BorderPane) StartView.this.getView();
                if(targetView!=viewEdit.getView() && pane.getCenter().equals(viewEdit.getView()) && !viewEdit.getDocument().trim().equals("")){
                    toolsGroup.getToggles().forEach(tog->tog.setSelected(false));
                    // 编辑页面的内容已经保存的时候，不在询问
                    if (viewEdit.isSaved()) {
                        config.publishEvent(new ResetEvent(StartEditView.class));
                        pane.setCenter(this.targetView);
                        toolsGroup.setUserData(obsButton);
                        toolsGroup.selectToggle(obsButton);
                        return;
                    }
                    UIUtil.showAlertDialog("你正在离开编辑页面，这样会失去正在编辑的内容，确定要这样吗？", "提示", Alert.AlertType.CONFIRMATION)
                            .ifPresent(btnSel->{
                                if(btnSel.equals(ButtonType.OK)) {
                                    config.publishEvent(new ResetEvent(StartEditView.class));
                                    pane.setCenter(this.targetView);
                                    toolsGroup.setUserData(obsButton);
                                    toolsGroup.selectToggle(obsButton);
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
                    initToolBtn(btn,"list", true);
                    btn.setSelected(true);
                    toolsGroup.setUserData(btn);
                    btn.selectedProperty().addListener(new ViewToolButtonHandler(btn,viewList.getView()));
                });

        Optional.ofNullable((ToggleButton) findById("write",tool.getItems()))
                .ifPresent(btn-> {
                    initToolBtn(btn,"file", true);
                    btn.selectedProperty().addListener(new ViewToolButtonHandler(btn,viewEdit.getView()));
                });

        Optional.ofNullable((ToggleButton)findById("read",tool.getItems()))
                .ifPresent(btn->{
                    initToolBtn(btn,"book", true);
                    btn.selectedProperty().addListener(new ViewToolButtonHandler(btn,viewRead.getView()));
                });

        Optional.ofNullable((ToggleButton) findById("config",tool.getItems()))
                .ifPresent(btn->{
                    initToolBtn(btn,"cog", true);
                    btn.selectedProperty().addListener(new ViewToolButtonHandler(btn,viewConfig.getView()));
                });
        VBox option = (VBox) findById("option", tool.getItems());
        Optional.ofNullable((ToggleButton)findById("tree", option.getChildren())).ifPresent(btn -> {
            initToolBtn(btn, "tree", false);
            btn.selectedProperty().addListener((observable, oldValue, newValue) -> {
               Optional.ofNullable(newValue).ifPresent(val -> {
                   if (val) {
                       config.publishEvent(new ViewChangeEvent("ShowTree"));
                   } else {
                       config.publishEvent(new ViewChangeEvent("HideTree"));
                   }
               });
            });
        });
        Button btnSearch = (Button) getView().lookup("#search");
        btnSearch.setFont(UIConfig.getFontIcon());
        btnSearch.setText(String.valueOf(UIConfig.getAwesomeMap().get("search")));
        BorderPane leftRoot = (BorderPane) this.getView().lookup("#leftRoot");
        this.typeTreePanel = (VBox)leftRoot.getCenter();
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
            refreshWidths();
        });
        pane.heightProperty().addListener(num->{
            ((BorderPane) viewList.getView()).setPrefHeight(pane.getHeight());
            ((BorderPane) viewEdit.getView()).setPrefHeight(pane.getHeight());
            ((BorderPane) viewRead.getView()).setPrefHeight(pane.getHeight());
            ((BorderPane) viewConfig.getView()).setPrefHeight(pane.getHeight());
        });
    }

    public void refreshWidths(){
        BorderPane pane = (BorderPane) this.getView();
        ((BorderPane) viewList.getView()).setPrefWidth(pane.getWidth() - ((BorderPane)pane.getLeft()).getPrefWidth());
        ((BorderPane) viewEdit.getView()).setPrefWidth(pane.getWidth() - ((BorderPane)pane.getLeft()).getPrefWidth());
        ((BorderPane) viewRead.getView()).setPrefWidth(pane.getWidth() - ((BorderPane)pane.getLeft()).getPrefWidth());
        ((BorderPane) viewConfig.getView()).setPrefWidth(pane.getWidth() - ((BorderPane)pane.getLeft()).getPrefWidth());
    }

    private void initToolBtn(ToggleButton btn,String iconName, boolean single){
        btn.setText(String.valueOf(UIConfig.getAwesomeMap().get(iconName)));
        if (single) {
            toolsGroup.getToggles().add(btn);
            btn.setFont(UIConfig.getFontIcon());
        } else {
            btn.setFont(UIConfig.getFontIconVerySmall());
        }
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
                       // pane.setCenter(viewEdit.getView());
                    });
        }else if(e.getViewName().equals("ListView")){
            Optional.ofNullable((ToggleButton) findById("list",tool.getItems()))
                    .ifPresent(btn-> {
                        toolsGroup.selectToggle(btn);
                      //  pane.setCenter(viewList.getView());
                    });
        }else if(e.getViewName().equals("ReadView")){
            Optional.ofNullable((ToggleButton)findById("read",tool.getItems()))
                    .ifPresent(btn->{
                        toolsGroup.selectToggle(btn);
                       // pane.setCenter(viewRead.getView());
                    });
        } else if(e.getSource().equals("HideTree")) {
            BorderPane leftRoot = (BorderPane) this.getView().lookup("#leftRoot");
            leftRoot.setCenter(null);
            leftRoot.setPrefWidth(60);
            refreshWidths();
        } else if (e.getSource().equals("ShowTree")) {
            BorderPane leftRoot = (BorderPane) this.getView().lookup("#leftRoot");
            leftRoot.setCenter(this.typeTreePanel);
            leftRoot.setPrefWidth(297);
           refreshWidths();
        }
    }
}
