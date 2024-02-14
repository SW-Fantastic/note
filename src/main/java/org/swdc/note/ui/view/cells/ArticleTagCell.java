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

public class ArticleTagCell extends ListCell<ShortArticleTag> {

    private MaterialIconsService iconsService;

    private HBox tagRoot;

    private Label label;

    private TopicSubViewController viewController;

    public ArticleTagCell(MaterialIconsService iconsService,TopicSubViewController controller) {
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
            label = new Label();
            tagRoot.setAlignment(Pos.CENTER_LEFT);
            HBox right = new HBox();
            HBox.setHgrow(right, Priority.ALWAYS);
            right.setFillHeight(true);
            right.setAlignment(Pos.CENTER_RIGHT);
            HBox.setMargin(right, new Insets(0,0,0,12));

            Button remove = new Button();
            remove.setPadding(new Insets(2));
            remove.setFont(iconsService.getFont(FontSize.SMALL));
            remove.setText(iconsService.getFontIcon("delete"));
            remove.setOnAction(e -> {
                viewController.removeTag(getItem());
            });
            right.getChildren().add(remove);

            tagRoot.getChildren().addAll(label,right);
            tagRoot.prefHeightProperty().bind(heightProperty().subtract(12));
        }

        label.setText(item.getName());
        setGraphic(tagRoot);

    }
}
