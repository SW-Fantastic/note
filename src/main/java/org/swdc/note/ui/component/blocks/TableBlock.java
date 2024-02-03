package org.swdc.note.ui.component.blocks;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableBlock extends ArticleBlock {

    public static class TableData {

        private int columnCounts;

        private List<Map<Integer,String>> data;

        public TableData(int columnCounts, List<Map<Integer,String>> data) {
            this.columnCounts = columnCounts;
            this.data = data;
        }

        public int getColumnCounts() {
            return columnCounts;
        }

        public void setColumnCounts(int columnCounts) {
            this.columnCounts = columnCounts;
        }

        public List<Map<Integer, String>> getData() {
            return data;
        }

        public void setData(List<Map<Integer, String>> data) {
            this.data = data;
        }
    }

    private TableView<Map<Integer,String>> tableView;

    private BlockTableContextMenu contextMenu;

    private Map<Integer, Map<Integer,Double>> rowHeightPerColumn = new HashMap<>();

    public TableBlock() {
        this.contextMenu = new BlockTableContextMenu(this);
    }

    private TableColumn<Map<Integer,String>,String> createTableCol() {
        TableColumn<Map<Integer,String>,String> col = new TableColumn<>();
        col.setCellFactory(c -> new BlockTableCell(
                this,
                blocksEditor().getIconsService(),
                this.contextMenu
        ));
        col.setPrefWidth(100);
        return col;
    }

    public void insertColumn(int index) {
        blocksEditor().setChanged(true);
        if (index < 0 || index > tableView.getColumns().size()) {
            tableView.getColumns().add(createTableCol());
        } else {
            tableView.getColumns().add(index,createTableCol());
        }
    }

    public void removeColumn(int index) {
        blocksEditor().setChanged(true);
        tableView.getColumns().remove(index);
        tableView.getItems().forEach(e -> {
            e.remove(index);
        });
        if (tableView.getColumns().isEmpty()) {
            remove();
        }
    }

    public void addRow(int index) {
        blocksEditor().setChanged(true);
        Map<Integer,String> map = new HashMap<>();
        for (int idx = 0; idx < tableView.getColumns().size(); idx ++) {
            map.put(idx,"");
        }
        if (index < 0 || index > tableView.getItems().size()) {
            tableView.getItems().add(map);
        } else {
            tableView.getItems().add(index,map);
        }
        tableView.refresh();
    }

    public void removeRow(int index) {
        blocksEditor().setChanged(true);
        tableView.getItems().remove(index);
        rowHeightPerColumn.remove(index);
        if (tableView.getItems().isEmpty()) {
            remove();
        }
    }

    @Override
    public Node getEditor() {
        if (tableView == null) {
            tableView = new TableView<>();
            tableView.setMinHeight(34);
            tableView.setPrefHeight(34);
            tableView.getColumns()
                    .addAll(
                            createTableCol(),
                            createTableCol()
                    );
            tableView.getItems().addListener((ListChangeListener<? super Map<Integer, String>>) c -> {
                resized();
            });
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            tableView.getItems().add(new HashMap<>());
        }
        return tableView;
    }

    public void rowHeightChange(int row,int col, double height) {
        Map<Integer,Double> heightPerColumn = rowHeightPerColumn.computeIfAbsent(row, v-> new HashMap<>());
        heightPerColumn.put(col,height);
        resized();
    }

    private void resized() {
        double defaultItems = tableView.getItems().size() - rowHeightPerColumn.size();
        double resized = 0;
        for (Map<Integer,Double> colsPreRow: rowHeightPerColumn.values()) {
            double max = 0;
            for (Double val: colsPreRow.values()) {
                if (max < val) {
                    max = val;
                }
            }
            resized = resized + max;
        }
        tableView.setPrefHeight((defaultItems + 1) * 40 + resized);
    }

    @Override
    public BlockData<TableData> getData() {
        return new BlockData<>(
                this,
                new TableData(tableView.getColumns().size(),tableView.getItems())
        );
    }

    @Override
    protected String generate() {
        StringBuilder builder = new StringBuilder();
        for (int idx = 0; idx < tableView.getColumns().size(); idx ++) {
            if (idx == 0) {
                builder.append("|");
            }
            builder.append(":-:|");
        }
        builder.append("\n");
        for (int rowIdx = 0; rowIdx < tableView.getItems().size(); rowIdx ++) {
            Map<Integer,String> map = tableView.getItems().get(rowIdx);
            for (int idx = 0; idx < tableView.getColumns().size(); idx ++) {
                if (idx == 0) {
                    builder.append("|");
                }
                builder.append(map.get(idx)).append("|");
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public void setData(BlockData data) {
        if (tableView == null) {
            getEditor();
        }
        rowHeightPerColumn.clear();
        tableView.getColumns().clear();
        tableView.getItems().clear();

        Map content = (Map) data.getContent();
        int count = (Integer)content.get("columnCounts");
        List<TableColumn<Map<Integer,String>,String>> list = new ArrayList<>();
        for (int idx = 0; idx < count; idx ++) {
            tableView.getColumns().add(
                    idx,createTableCol()
            );
        }
        for (Map<String,String> row : (List<Map<String,String>>) content.get("data")) {
            Map<Integer,String> dataRow = new HashMap<>();
            for (int idx = 0; idx < count; idx ++) {
                dataRow.put(idx,row.get(idx + ""));
            }
            tableView.getItems().add(dataRow);
        }

        tableView.refresh();
    }
}
