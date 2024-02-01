package org.swdc.note.ui.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.note.ui.component.blocks.ArticleBlock;
import org.swdc.note.ui.component.blocks.BlockContextMenu;

public class ArticleBlockCell extends ListCell<ArticleBlock> {

    private HBox content;

    private HBox leftSideBlock;

    private MaterialIconsService iconsService;

    private ArticleBlocksEditor editor;

    private BlockContextMenu contextMenu;

    public ArticleBlockCell(ArticleBlocksEditor editor, MaterialIconsService iconsService) {
        this.iconsService = iconsService;
        this.editor = editor;
        this.contextMenu = new BlockContextMenu(editor);
    }

    @Override
    protected void updateItem(ArticleBlock item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {

            if (content == null) {
                content = new HBox();
                leftSideBlock = new HBox();

                leftSideBlock.setMinWidth(80);
                leftSideBlock.setMaxWidth(80);

                Button move = new Button();
                move.getStyleClass().add("edit-icon");
                move.setPadding(new Insets(4));
                move.setFont(iconsService.getFont(FontSize.SMALL));
                move.setText(iconsService.getFontIcon("apps"));

                Button add = new Button();
                add.getStyleClass().add("edit-icon");
                add.setPadding(new Insets(4));
                add.setFont(iconsService.getFont(FontSize.SMALL));
                add.setText(iconsService.getFontIcon("add"));
                add.setOnAction(e -> contextMenu.show(this.getIndex(),add));

                leftSideBlock.getChildren().addAll(add,move);
                leftSideBlock.setPadding(new Insets(2));
                leftSideBlock.setSpacing(4);
                leftSideBlock.setAlignment(Pos.TOP_CENTER);
                leftSideBlock.setVisible(false);

                content.setOnMouseEntered(e -> leftSideBlock.setVisible(true));
                content.setOnMouseExited(e -> leftSideBlock.setVisible(contextMenu.isShowing()));
                contextMenu.setOnHidden(e -> leftSideBlock.setVisible(false));
            }

            content.prefHeightProperty().unbind();
            content.getChildren().clear();

            Node node = item.getEditor();
            if (node instanceof Region) {
                Region region = (Region) node;
                region.prefWidthProperty().unbind();
                content.prefHeightProperty().bind(region.prefHeightProperty());
                region.prefWidthProperty().bind(content.widthProperty().subtract(24)
                        .subtract(leftSideBlock.widthProperty()));
            }
            content.getChildren().addAll(leftSideBlock,node);
            setGraphic(content);
        }
    }
}
