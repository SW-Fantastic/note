package org.swdc.note.ui.view.cells;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.note.core.entities.ShortArticle;
import org.swdc.note.core.entities.ShortArticleTag;

import java.text.SimpleDateFormat;

public class ShortArticleCell extends ListCell<ShortArticle> {

    private BorderPane root;

    private Label date;

    private TextArea content;

    private Text text;

    private ListView<ShortArticleTag> tags;

    private MaterialIconsService iconsService;

    public ShortArticleCell(MaterialIconsService iconsService) {
        this.iconsService = iconsService;
    }

    @Override
    protected void updateItem(ShortArticle item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            return;
        }
        if (root == null) {
            root = new BorderPane();

            text = new Text();
            content = new TextArea();

            date = new Label();
            date.setPadding(new Insets(8));

            content.setWrapText(true);
            content.setEditable(false);
            content.textProperty().addListener(e -> {
                text.getLayoutBounds();
            });

            text.wrappingWidthProperty().bind(root.widthProperty().subtract(20));
            text.fontProperty().bind(content.fontProperty());
            text.textProperty().bind(content.textProperty());
            text.layoutBoundsProperty().addListener(e -> {
                content.setPrefHeight(text.getLayoutBounds().getHeight() + 20);
            });

            tags = new ListView<>();
            tags.setOrientation(Orientation.HORIZONTAL);
            tags.setMaxHeight(34);
            HBox.setHgrow(tags, Priority.ALWAYS);

            HBox bottom = new HBox();
            bottom.setAlignment(Pos.CENTER_LEFT);
            bottom.getChildren().add(date);
            bottom.getStyleClass().add("bottom");

            HBox top = new HBox();
            top.setAlignment(Pos.CENTER_LEFT);

            HBox topAligned = new HBox();
            topAligned.setAlignment(Pos.CENTER_RIGHT);

            Button trash = new Button();
            trash.setPadding(new Insets(4));
            trash.setFont(iconsService.getFont(FontSize.MIDDLE_SMALL));
            trash.setText(iconsService.getFontIcon("delete"));

            topAligned.getChildren().add(trash);

            top.getChildren().addAll(tags,topAligned);

            BorderPane inner = new BorderPane();

            inner.setCenter(content);
            inner.setTop(top);
            inner.setBottom(bottom);
            inner.getStyleClass().add("short-cell");
            inner.setPadding(new Insets(4));

            root.setPadding(new Insets(8));
            root.setCenter(inner);
            root.prefWidthProperty().bind(widthProperty().subtract(60));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(item.getDate());
        this.date.setText(date);
        this.content.setText(item.getContent());
        //this.content.setPrefHeight(text.getLayoutBounds().getHeight());

        ObservableList<ShortArticleTag> tagsList = this.tags.getItems();
        tagsList.clear();
        tagsList.addAll(item.getTags());

        setGraphic(root);
    }
}
