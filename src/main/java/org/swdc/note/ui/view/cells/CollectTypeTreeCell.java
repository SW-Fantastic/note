package org.swdc.note.ui.view.cells;

import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.*;
import org.swdc.note.core.entities.CollectionArticle;
import org.swdc.note.core.entities.CollectionType;
import org.swdc.note.core.service.CollectionService;
import org.swdc.note.ui.view.UIUtils;

public class CollectTypeTreeCell extends TreeCell<CollectionType> {

    private Label label;

    public static final DataFormat DATA_ARTICLE_TYPE = new DataFormat("application/x-articleType");
    public static final DataFormat DATA_ARTICLE = new DataFormat("application/x-articleCollect");

    private CollectionService service;

    public CollectTypeTreeCell(CollectionService articleService) {
        this.service = articleService;
    }

    @Override
    protected void updateItem(CollectionType t, boolean empty) {
        super.updateItem(t,empty);
        if (empty) {
            setGraphic(null);
            return;
        }
        String string = t.toString();
        if (label == null) {
            label = new Label();
            label.setOnDragDetected(this::dragDetected);
            label.setOnDragOver(this::dragOver);
            label.setOnDragDropped(this::dragDropped);
        }
        label.setText(string);
        setGraphic(label);
    }

    private void dragDropped(DragEvent event) {

        event.consume();
        Dragboard dragboard = event.getDragboard();

        if (dragboard.hasContent(DATA_ARTICLE_TYPE)) {

            Object dragged = dragboard.getContent(DATA_ARTICLE_TYPE);

            if (dragged == null) {
                return;
            }
            String typeId = dragged.toString();
            CollectionType type = getItem();
            if (type == null || type.getId().equals(typeId)) {
                return;
            }
            CollectionType moved = service.getType(typeId);

            TreeItem<CollectionType> parent = getTreeView().getRoot();
            if (moved.getParent() != null) {
                parent = UIUtils.findTypeItem(
                        getTreeView().getRoot(),
                        moved.getParent(),
                        CollectionType::getId
                );
            }

            TreeItem<CollectionType> movedItem = UIUtils.findTypeItem(
                    getTreeView().getRoot(),
                    moved,
                    CollectionType::getId
            );

            if (parent != null && movedItem != null) {
                parent.getChildren().remove(movedItem);
            }

            moved.setParent(type);
            moved = service.saveType(moved);
            getTreeItem().getChildren()
                    .add(UIUtils.createTypeTree(moved));

            event.setDropCompleted(true);

        } else if (dragboard.hasContent(DATA_ARTICLE)) {

            String articleId = dragboard.getContent(DATA_ARTICLE).toString();
            CollectionArticle article = service.getArticleById(articleId);
            if (!getItem().getId().equals(article.getType().getId())) {
                article.setType(this.getItem());
                service.saveCollection(article);
                event.setDropCompleted(true);
            }

        }

    }

    private void dragOver(DragEvent event) {

        event.acceptTransferModes(TransferMode.MOVE);

    }

    private void dragDetected(MouseEvent mouseEvent) {

        if (label == null || this.isEmpty()) {
            return;
        }

        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.put(DATA_ARTICLE_TYPE,getItem().getId());
        Dragboard dragboard = label.startDragAndDrop(TransferMode.MOVE);
        dragboard.setContent(clipboardContent);
        mouseEvent.consume();

    }

}
