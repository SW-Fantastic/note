package org.swdc.note.ui.view.cells;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.ListCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import org.swdc.note.core.entities.ArticleType;

import java.util.List;

public class ArticleTypeListCell extends ListCell<ArticleType> {

    private ArticleTypeCell typeCell;

    public ArticleTypeListCell(ArticleTypeCell view) {
        this.typeCell = view;
        this.setOnDragOver(this::onDragOver);
        this.setOnDragDropped(this::onDragDropped);
    }

    private void onDragOver(DragEvent dragEvent) {
        if (this.getItem() == null) {
            return;
        }
        if (dragEvent.getDragboard().hasString()) {
            dragEvent.acceptTransferModes(TransferMode.MOVE);
        }
    }

    private void onDragDropped(DragEvent dragEvent) {
        if (this.getItem() == null) {
            return;
        }
        if (!dragEvent.getDragboard().hasString()) {
            return;
        }
        Dragboard dragboard = dragEvent.getDragboard();
        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().constructParametricType(List.class,String.class);
        try {
            List<String> list = mapper.readValue(dragboard.getString(),type);
            for (String data: list) {
                ArticleCellDragData dragData = ArticleCellDragData.fromData(data);
                if (dragData.getArticleTypeId().equals(this.getItem().getId())) {
                    continue;
                }
                typeCell.updateArticle(dragData.getArticleId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(ArticleType type, boolean b) {
        super.updateItem(type, b);
        if (b) {
            setGraphic(null);
            return;
        }
        typeCell.setType(type);
        HBox content = typeCell.getView();
        content.prefWidthProperty().bind(this.getListView().widthProperty().subtract(36));
        setGraphic(content);
    }
}
