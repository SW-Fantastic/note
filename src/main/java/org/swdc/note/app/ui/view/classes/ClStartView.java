package org.swdc.note.app.ui.view.classes;

import static org.swdc.note.app.util.UIUtil.findById;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.event.ViewChangeEvent;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.StartConfigView;
import org.swdc.note.app.ui.view.StartEditView;
import org.swdc.note.app.ui.view.StartReadView;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Conditional(NotViewNormalCondition.class)
@FXMLView("/view/classes/legStartView.fxml")
public class ClStartView extends AbstractFxmlView{

    @Autowired
    private ClListView viewList;

    @Autowired
    private StartEditView viewEdit;

    @Autowired
    private StartReadView viewRead;

    @Autowired
    private StartConfigView viewConfig;

    @Autowired
    private UIConfig config;

    @PostConstruct
    protected void initUI() throws Exception{
        GUIState.getStage().setTitle("幻想笔记");
        GUIState.getStage().setMinWidth(1020);
        GUIState.getStage().setMinHeight(680);
        BorderPane pane = (BorderPane) this.getView();
        String res = new StringBuilder(UIConfig.getConfigLocation()).append("res/").append(config.getBackground()).toString();
        pane.setStyle(pane.getStyle()+";-fx-background-image: url("+res+");");

        UIUtil.configTheme(pane,config);

        ToolBar tool = (ToolBar) getView().lookup(".tool");

        // 使用font-awsome的字体图标
        Optional.ofNullable((Button) findById("list",tool.getItems()))
                .ifPresent(btn-> {
                    initToolBtn(btn,"plus");
                    btn.setOnAction(e->{
                        if(viewEdit.getStage().isShowing()){
                            viewEdit.getStage().requestFocus();
                        }else{
                            viewEdit.getStage().show();
                        }
                    });
                });

        Optional.ofNullable((Button) findById("write",tool.getItems()))
                .ifPresent(btn-> {
                    initToolBtn(btn,"desktop");
                    btn.setOnAction(e->{
                        Stage stage = viewRead.getStage();
                        if(stage.isShowing()){
                            stage.requestFocus();
                        }else{
                            stage.show();
                        }
                    });
                });

        Optional.ofNullable((Button)findById("read",tool.getItems()))
                .ifPresent(btn->{
                    initToolBtn(btn,"book");
                });

        Optional.ofNullable((Button) findById("config",tool.getItems()))
                .ifPresent(btn->{
                    initToolBtn(btn,"cog");
                });

        Button btnSearch = (Button) getView().lookup("#search");
        btnSearch.setFont(UIConfig.getFontIcon());
        btnSearch.setText(String.valueOf(UIConfig.getAwesomeMap().get("search")));
        pane.setCenter(viewList.getView());
        pane.widthProperty().addListener(num->{
            ((BorderPane) viewList.getView()).setPrefWidth(pane.getWidth() - ((StackPane)pane.getLeft()).getPrefWidth());
        });
        pane.heightProperty().addListener(num->{
            ((BorderPane) viewList.getView()).setPrefHeight(pane.getHeight() - tool.getHeight());
        });
    }

    private void initToolBtn(Button btn,String iconName){
        btn.setFont(UIConfig.getFontIcon());
        btn.setText(String.valueOf(UIConfig.getAwesomeMap().get(iconName)));
    }

    /**
     * 界面需要发生变化
     * @param e
     */
    @EventListener
    public void onViewChange(ViewChangeEvent e){
        ToolBar tool = (ToolBar) getView().lookup(".tool");

        if(e.getViewName().equals("EditView")){
            Optional.ofNullable((Button) findById("write",tool.getItems()))
                    .ifPresent(btn-> {

                    });
        }else if(e.getViewName().equals("ListView")){
            Optional.ofNullable((Button) findById("list",tool.getItems()))
                    .ifPresent(btn-> {

                    });
        }else if(e.getViewName().equals("ReadView")){
            Optional.ofNullable((Button)findById("read",tool.getItems()))
                    .ifPresent(btn->{
                    });
        }
    }

}
