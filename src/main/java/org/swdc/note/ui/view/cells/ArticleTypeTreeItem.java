package org.swdc.note.ui.view.cells;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.view.UIUtils;

import java.util.List;

public class ArticleTypeTreeItem extends TreeCell<ArticleType> {

    private Label label;

    private ArticleService articleService;

    private static Logger logger = LoggerFactory.getLogger(ArticleTypeTreeItem.class);

    public static final DataFormat DATA_EDIT_TYPE = new DataFormat("application/x-edit-article");
    public static final DataFormat DATA_EDIT = new DataFormat("application/x-edit-articleCollect");

    public ArticleTypeTreeItem(ArticleService service) {
        this.articleService = service;
    }

    @Override
    protected void updateItem(ArticleType item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            return;
        }
        if (label == null) {
            label = new Label();
            label.setOnDragDetected(this::dragDetected);
            label.setOnDragOver(this::dragOver);
            label.setOnDragDropped(this::dragDropped);
        }
        label.setText(getItem().toString());
        setGraphic(label);
    }

    private void dragDropped(DragEvent event) {
        event.consume();
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasContent(DATA_EDIT_TYPE)) {

            String movedTypeId = dragboard.getContent(DATA_EDIT_TYPE).toString();
            ArticleType moved = articleService.getType(movedTypeId);

            TreeItem<ArticleType> parentItem = getTreeView().getRoot();
            if (moved.getParent() != null) {
                parentItem = UIUtils.findTypeItem(
                        getTreeView().getRoot(),
                        moved.getParent(),
                        ArticleType::getId
                );
            }

            TreeItem<ArticleType> movedItem = UIUtils.findTypeItem(
                    getTreeView().getRoot(),
                    moved,
                    ArticleType::getId
            );

            if (parentItem != null && movedItem != null) {
                parentItem.getChildren().remove(movedItem);
            }

            moved.setParent(this.getItem());
            moved = articleService.saveType(moved);

            getTreeItem().getChildren().add(
                    UIUtils.createTypeTree(moved)
            );

            event.setDropCompleted(true);
        } else if (dragboard.hasContent(DATA_EDIT)) {

            ObjectMapper mapper = new ObjectMapper();
            String articles = dragboard.getContent(DATA_EDIT).toString();
            try {
                JavaType type = mapper.getTypeFactory()
                        .constructParametricType(List.class,String.class);
                List<String> articleIds = mapper.readValue(articles,type);
                for (String artId : articleIds) {
                    Article article = articleService.getArticle(artId);
                    article.setType(getItem());
                    articleService.saveArticle(article,articleService.getContentOf(article));
                }
                event.setDropCompleted(true);
            } catch (Exception e) {
                logger.error("failed to drop article : " , e);
            }
        }
    }

    private void dragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.MOVE);
    }

    private void dragDetected(MouseEvent mouseEvent) {
        if (getItem() == null) {
            return;
        }
        ArticleType type = getItem();
        Dragboard dragboard = label.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.put(DATA_EDIT_TYPE,type.getId());
        dragboard.setContent(clipboardContent);
        mouseEvent.consume();
    }


}
