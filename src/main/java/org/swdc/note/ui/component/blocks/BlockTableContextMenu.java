package org.swdc.note.ui.component.blocks;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class BlockTableContextMenu extends ContextMenu {

    private int columnIndex;

    private int rowIndex;

    private TableBlock tableBlock;

    public BlockTableContextMenu(TableBlock tableBlock) {

        MenuItem itemInsertColumn = new MenuItem("插入列");
        itemInsertColumn.setOnAction(e -> {
            tableBlock.insertColumn(columnIndex);
        });

        MenuItem itemAddColumn = new MenuItem("添加列");
        itemAddColumn.setOnAction(e -> {
            tableBlock.insertColumn(columnIndex + 1);
        });

        MenuItem itemRemoveColumn = new MenuItem("删除列");
        itemRemoveColumn.setOnAction(e -> {
            tableBlock.removeColumn(columnIndex);
        });

        MenuItem itemInsertRow = new MenuItem("插入行");
        itemInsertRow.setOnAction(e -> {
            tableBlock.addRow(rowIndex);
        });

        MenuItem itemAddRow = new MenuItem("添加行");
        itemAddRow.setOnAction(e -> {
            tableBlock.addRow(rowIndex + 1);
        });

        MenuItem itemRemoveRow = new MenuItem("删除行");
        itemRemoveRow.setOnAction(e -> {
            tableBlock.removeRow(rowIndex);
        });

        getItems().addAll(
                itemInsertColumn,
                itemAddColumn,
                itemRemoveColumn,
                new SeparatorMenuItem(),
                itemInsertRow,
                itemAddRow,
                itemRemoveRow
        );

    }

    public void show(Node anchor, int columnIndex, int rowIndex) {
        this.columnIndex = columnIndex;
        this.rowIndex = rowIndex;
        this.show(anchor,Side.BOTTOM,0,0);
    }
}
