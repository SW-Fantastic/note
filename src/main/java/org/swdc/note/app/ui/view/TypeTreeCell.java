package org.swdc.note.app.ui.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.event.DeleteEvent;
import org.swdc.note.app.event.ExportEvent;
import org.swdc.note.app.ui.UIConfig;

/**
 *  分类树的格子。
 */
@Scope(value = "prototype")
@Component
public class TypeTreeCell extends TreeCell<ArticleType> {

    @Autowired
    private UIConfig config;

    @Override
    protected void updateItem(ArticleType item, boolean empty) {
        super.updateItem(item, empty);
        if(empty){
            setGraphic(null);
            return;
        }
        HBox hbxRoot = new HBox();
        HBox hbxLabel = new HBox();
        HBox hbxBtn = new HBox();
        HBox.setHgrow(hbxLabel, Priority.ALWAYS);
        HBox.setHgrow(hbxLabel, Priority.ALWAYS);

        Label lblName = new Label(item.toString());
        hbxLabel.getChildren().add(lblName);
        hbxRoot.getChildren().add(hbxLabel);

        this.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue!=null&& newValue){
                hbxBtn.setVisible(true);
            }else{
                hbxBtn.setVisible(false);
            }
        }));

        hbxBtn.setVisible(false);
        Insets insetsBtn = new Insets(0,0,0,4);
        Button btn = new Button();
        btn.setFont(UIConfig.getFontIconVerySmall());
        btn.setText(String.valueOf(UIConfig.getAwesomeMap().get("trash")));
        btn.setOnAction(e->{
            DeleteEvent deleteEvent = new DeleteEvent(item);
            config.publishEvent(deleteEvent);
        });
        HBox.setMargin(btn,insetsBtn);
        Button btnExport = new Button();
        btnExport.setFont(UIConfig.getFontIconVerySmall());
        btnExport.setText(String.valueOf(UIConfig.getAwesomeMap().get("sign_out")));
        HBox.setMargin(btnExport,insetsBtn);
        btnExport.setOnAction(e->{
            ExportEvent exportEvent = new ExportEvent(item);
            config.publishEvent(exportEvent);
        });
        hbxBtn.getChildren().add(btnExport);
        hbxBtn.getChildren().add(btn);
        hbxRoot.getChildren().add(hbxBtn);
        setGraphic(hbxRoot);
    }
}
