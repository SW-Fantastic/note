package org.swdc.note.ui.view.cells;

import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.swdc.note.core.entities.Article;

public class ArticleSimpleListCell extends ListCell<Article> {

    private ArticleSimpleCell simpleCell;

    public ArticleSimpleListCell(ArticleSimpleCell cell) {
        this.simpleCell = cell;
    }

    @Override
    protected void updateItem(Article article, boolean empty) {
        super.updateItem(article, empty);
        if (empty) {
            setGraphic(null);
            return;
        }
        simpleCell.setArticle(article);
        HBox box = (HBox) simpleCell.getView();
        box.prefWidthProperty().bind(getListView().widthProperty().subtract(24));
        setGraphic(box);
    }
}
