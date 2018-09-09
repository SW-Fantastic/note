package org.swdc.note.app.ui.controller;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.ui.view.ArtleCellView;

/**
 * 文档列表的单元格
 */
public class ArtleCell extends ListCell<Artle> {

    private ArtleCellView view;

    public ArtleCell(ArtleCellView parent) {
        this.view =  parent;
    }

    @Override
    protected void updateItem(Artle item, boolean empty) {
        super.updateItem(item,empty);
        BorderPane pane = (BorderPane) view.getView();
        pane.prefWidthProperty().bind(getListView().prefWidthProperty());
        if(!empty){
            view.setArtle(item);
            setGraphic(view.getView());
        }else{
            view.setArtle(null);
            setGraphic(view.getView());
        }
    }
}
