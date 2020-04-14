package org.swdc.note.ui.view.cells;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import org.swdc.note.core.entities.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleListCell extends ListCell<Article> {

    private ArticleCell cell;

    public ArticleListCell(ArticleCell cell) {
        this.cell = cell;
        this.setOnDragDetected(this::onDragStart);
    }

    private void onDragStart(MouseEvent e) {
        if (this.getItem() == null) {
            return;
        }

        ListView<Article> listView = getListView();
        List<Article> selection = listView.getSelectionModel().getSelectedItems();
        if (selection == null || selection.isEmpty()) {
            return;
        }
        List<String> dataList = new ArrayList<>();
        for (Article article: selection) {
            ArticleCellDragData dragData = new ArticleCellDragData(article);
            String data = dragData.asString();
            if (data == null) {
                continue;
            }
            dataList.add(data);
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(dataList);
            Dragboard dragboard = this.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(data);
            dragboard.setContent(content);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void updateItem(Article article, boolean empty) {
        super.updateItem(article, empty);
        if (empty) {
            setGraphic(null);
            return;
        }
        this.cell.setArticle(article);
        BorderPane view = (BorderPane) cell.getView();
        ListView<Article> listView = this.getListView();
        view.prefWidthProperty().bind(listView.widthProperty().subtract(36));
        setGraphic(view);
    }

}
