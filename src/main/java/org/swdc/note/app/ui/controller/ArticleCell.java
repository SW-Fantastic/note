package org.swdc.note.app.ui.controller;

import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.ui.view.ArticleCellView;

/**
 * 文档列表的格子
 */
public class ArticleCell extends ListCell<Article> {

    private ArticleCellView view;

    public ArticleCell(ArticleCellView parent) {
        this.view =  parent;
    }

    @Override
    protected void updateItem(Article item, boolean empty) {
        super.updateItem(item,empty);
        BorderPane pane = (BorderPane) view.getView();
        getListView().widthProperty().addListener((observable ->  pane.setPrefWidth(getListView().getWidth() - 64)));
        pane.setPrefWidth(getListView().getWidth() - 64);
        if(!empty){
            view.setArticle(item);
            setGraphic(view.getView());
        }else{
            view.setArticle(null);
            setGraphic(view.getView());
        }
    }
}
