package org.swdc.note.app.ui.controller;

import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.ui.view.ArtleCellView;

/**
 * Created by lenovo on 2018/9/8.
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
