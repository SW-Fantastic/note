package org.swdc.note.app.ui.controller;

import javafx.scene.control.TableCell;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.ui.view.ManageCellView;

/**
 * Created by lenovo on 2018/11/4.
 */
public class ManageCell extends TableCell<Article,Long> {

    private ManageCellView view;

    protected ManageCell(ManageCellView view){
        this.view = view;
    }

    @Override
    protected void updateItem(Long item, boolean empty) {
        super.updateItem(item, empty);
        if(empty){
            view.setArticle(null);
            setGraphic(null);
            return;
        }
        view.setArticle(this.getTableView().getItems().get(this.getIndex()));
        setGraphic(view.getView());
    }
}
