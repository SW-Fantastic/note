package org.swdc.note.app.ui.view.dialogs;

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
import org.swdc.note.app.event.TypeImportEvent;
import org.swdc.note.app.ui.UIConfig;

/**
 *  文档集的分类树格子
 */
@Scope(value = "prototype")
@Component
public class ArticleSetTreeCell extends TreeCell<ArticleType> {

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

        Button btnImport = new Button();
        btnImport.setFont(UIConfig.getFontIconVerySmall());
        btnImport.setText(String.valueOf(UIConfig.getAwesomeMap().get("download")));
        HBox.setMargin(btnImport,insetsBtn);
        btnImport.setOnAction(e->{
            TypeImportEvent importEvent = new TypeImportEvent(item);
            config.publishEvent(importEvent);
        });
        hbxBtn.getChildren().add(btnImport);
        hbxRoot.getChildren().add(hbxBtn);
        setGraphic(hbxRoot);
    }

}
