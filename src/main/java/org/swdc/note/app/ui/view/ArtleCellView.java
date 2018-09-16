package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.event.ArtleEditEvent;
import org.swdc.note.app.event.ArtleOpenEvent;
import org.swdc.note.app.event.ViewChangeEvent;
import org.swdc.note.app.ui.UIConfig;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;

/**
 * 文档列表的单元格的视图
 * prototype模式，应该使用UIConfig获取
 */
@FXMLView("/view/artleCell.fxml")
@Scope(value = "prototype")
public class ArtleCellView extends AbstractFxmlView{

    @Autowired
    private UIConfig config;

    private Artle artle;

    @PostConstruct
    protected void initUI(){
        Button btnIcon = (Button)getView().lookup("#icon");
        btnIcon.setFont(UIConfig.getFontIconSmall());
        btnIcon.setText(String.valueOf(UIConfig.getAwesomeMap().get("file")));
        btnIcon.setVisible(true);
        VBox vbx = (VBox)getView().lookup("#bg");
        vbx.setVisible(true);
        Label lblArrow = (Label)getView().lookup("#arrow");
        lblArrow.setText(String.valueOf(UIConfig.getAwesomeMap().get("caret_left")));
        lblArrow.setFont(UIConfig.getFontIconLarge());
        Button btnRead = (Button) getView().lookup("#read");
        btnRead.setTextFill(Color.DARKGREEN);
        btnRead.setFont(UIConfig.getFontIconSmall());
        btnRead.setText(String.valueOf(UIConfig.getAwesomeMap().get("folder_open")));
        btnRead.setOnAction(e->{
            if(artle!=null){
                // 发出事件，通知其他组件打开此文档
                config.publishEvent(new ArtleOpenEvent(artle));
            }
        });
        Button btnDel = (Button)getView().lookup("#delete");
        btnDel.setTextFill(Color.DARKRED);
        btnDel.setFont(UIConfig.getFontIconSmall());
        btnDel.setText(String.valueOf(UIConfig.getAwesomeMap().get("trash")));
        Button btnEdit = (Button)getView().lookup("#edit");
        btnEdit.setTextFill(Color.DARKBLUE);
        btnEdit.setFont(UIConfig.getFontIconSmall());
        btnEdit.setText(String.valueOf(UIConfig.getAwesomeMap().get("pencil")));
        btnEdit.setOnAction(e->{
            if(artle != null){
                // 发出事件，通知其他组件转到编辑状态
                config.publishEvent(new ArtleEditEvent(artle));
            }
        });
    }

    public void setArtle(Artle artle){
        this.artle = artle;
        Button btnIcon = (Button)getView().lookup("#icon");
        VBox vbx = (VBox)getView().lookup("#bg");
        Label lblArrow = (Label)getView().lookup("#arrow");
        Label lblTitle = (Label)getView().lookup("#title");
        Label lblDate = (Label)getView().lookup("#date");
        HBox hbx = (HBox)getView().lookup("#editPane");
        if(artle == null){
           btnIcon.setVisible(false);
            vbx.setVisible(false);
            lblArrow.setVisible(false);
            hbx.setVisible(false);
            lblTitle.setText("");
            lblDate.setText("");
            return;
        }
        lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(artle.getCreatedDate()));
        lblTitle.setText(artle.getTitle());
        btnIcon.setVisible(true);
        vbx.setVisible(true);
        lblArrow.setVisible(true);
        hbx.setVisible(true);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        BorderPane pane = (BorderPane)this.getView();
        pane.prefWidthProperty().unbind();
    }
}
