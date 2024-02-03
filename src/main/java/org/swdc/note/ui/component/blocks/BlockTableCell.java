package org.swdc.note.ui.component.blocks;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;

import java.util.Map;

public class BlockTableCell extends TableCell<Map<Integer,String>,String> {


    private TextArea field;

    private HBox root;

    private MaterialIconsService materialIconsService;

    private BlockTableContextMenu contextMenu;

    private TableBlock block;

    public BlockTableCell(TableBlock tableBlock, MaterialIconsService iconsService, BlockTableContextMenu menu) {
        this.materialIconsService = iconsService;
        this.contextMenu = menu;
        this.block = tableBlock;
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {

            TableColumn<Map<Integer,String>,String> col = getTableColumn();

            if (field == null) {
                root = new HBox();

                field = new TextArea();
                field.setMinHeight(28);
                field.setPrefHeight(28);
                field.setWrapText(true);
                Text textHolder = new Text();
                textHolder.textProperty().bind(field.textProperty());
                textHolder.fontProperty().bind(field.fontProperty());
                textHolder.wrappingWidthProperty().bind(field.widthProperty().subtract(40));
                textHolder.layoutBoundsProperty().addListener(c -> {
                    double height = textHolder.getLayoutBounds().getHeight() + 20;
                    field.setPrefHeight(height);
                    block.rowHeightChange(
                            getIndex(),
                            getTableView().getColumns().indexOf(col),
                            height + 12
                    );
                });

                field.textProperty().addListener(e -> {
                    textHolder.getLayoutBounds();
                    int columnIndex = getTableView().getColumns().indexOf(col);
                    Map<Integer,String> theRow = getTableRow().getItem();
                    if (!theRow.containsKey(columnIndex) || !field.getText().equals(theRow.get(columnIndex))) {
                        theRow.put(columnIndex,field.getText());
                    }
                });

                HBox.setHgrow(field, Priority.ALWAYS);

                Button menus = new Button();
                menus.setVisible(false);
                menus.setPadding(new Insets(4));
                menus.setFont(materialIconsService.getFont(FontSize.SMALL));
                menus.setText(materialIconsService.getFontIcon("more_horiz"));
                menus.getStyleClass().add("edit-icon");
                menus.setOnAction(e -> {
                    int columnIndex = getTableView().getColumns().indexOf(col);
                    contextMenu.show(menus,columnIndex,getIndex());
                });

                root.setAlignment(Pos.CENTER);
                root.setOnMouseEntered(e -> menus.setVisible(true));
                root.setOnMouseExited(e -> menus.setVisible(contextMenu.isShowing()));
                root.getStyleClass().add("cell-box");
                root.setSpacing(4);
                root.setPadding(new Insets(0,2,0,2));
                contextMenu.setOnHidden(e -> menus.setVisible(false));

                root.getChildren().addAll(
                        field,
                        menus
                );

            }
            Map<Integer,String> theRow = getTableRow().getItem();
            field.setText(theRow.computeIfAbsent(getTableView().getColumns().indexOf(col),v -> ""));
            setGraphic(root);
        }
    }
}
