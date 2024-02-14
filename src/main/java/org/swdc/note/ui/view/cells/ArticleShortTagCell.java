package org.swdc.note.ui.view.cells;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.note.core.entities.ShortArticleTag;
import org.swdc.note.ui.controllers.TopicSubViewController;

public class ArticleShortTagCell extends ListCell<ShortArticleTag> {

    private HBox tagRoot;

    private Label label;

    private MaterialIconsService iconsService;

    private TopicSubViewController viewController;

    public ArticleShortTagCell(MaterialIconsService iconsService,TopicSubViewController controller) {
        this.iconsService = iconsService;
        this.viewController = controller;
    }

    @Override
    protected void updateItem(ShortArticleTag item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            return;
        }

        if (tagRoot == null) {
            tagRoot = new HBox();
            HBox content = new HBox();

            label = new Label();
            label.getStyleClass().add("date-label");
            content.getStyleClass().add("tag-cell");
            content.setAlignment(Pos.CENTER_LEFT);

            Button remove = new Button();
            remove.setPadding(new Insets(2));
            remove.setFont(iconsService.getFont(FontSize.SMALL));
            remove.setText(iconsService.getFontIcon("delete"));
            remove.setOnAction(e -> {
                viewController.trashTag(getItem());
            });

            content.setSpacing(12);
            content.setPadding(new Insets(2,4,2,4));
            content.getChildren().addAll(label,remove);
            HBox.setHgrow(content,Priority.NEVER);
            tagRoot.getChildren().add(content);
        }

        label.setText("# " + item.getName());
        setGraphic(tagRoot);
    }
}
