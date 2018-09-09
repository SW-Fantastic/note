package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.swdc.note.app.entity.Artle;
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

    @PostConstruct
    protected void initUI(){
        Button btnIcon = (Button)getView().lookup("#icon");
        btnIcon.setFont(UIConfig.getFontIconSmall());
        btnIcon.setText(String.valueOf(UIConfig.getGLYPH_MAP().get("file")));
        btnIcon.setVisible(true);
        VBox vbx = (VBox)getView().lookup("#bg");
        vbx.setVisible(true);
        Label lblArrow = (Label)getView().lookup("#arrow");
        lblArrow.setText(String.valueOf(UIConfig.getGLYPH_MAP().get("caret_left")));
        lblArrow.setFont(UIConfig.getFontIconLarge());
    }

    public void setArtle(Artle artle){
        Button btnIcon = (Button)getView().lookup("#icon");
        VBox vbx = (VBox)getView().lookup("#bg");
        Label lblArrow = (Label)getView().lookup("#arrow");
        Label lblTitle = (Label)getView().lookup("#title");
        Label lblDate = (Label)getView().lookup("#date");
        if(artle == null){
           btnIcon.setVisible(false);
            vbx.setVisible(false);
            lblArrow.setVisible(false);
            lblTitle.setText("");
            lblDate.setText("");
            return;
        }
        lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(artle.getCreatedDate()));
        lblTitle.setText(artle.getTitle());
        btnIcon.setVisible(true);
        vbx.setVisible(true);
        lblArrow.setVisible(true);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        BorderPane pane = (BorderPane)this.getView();
        pane.prefWidthProperty().unbind();
    }
}
