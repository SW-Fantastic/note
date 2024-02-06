package org.swdc.note.ui.component.blocks;

import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.scene.control.ListView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListBlock extends ArticleBlock {

    private static final String TYPE = "LIST";

    private ListView<String> listView;

    private Map<Integer,Double> rowHeights = new HashMap<>();

    @Override
    public Node getEditor() {
        if (listView == null) {
            listView = new ListView<>();
            listView.setCellFactory(c -> new ListBlockCell(this));
            listView.getItems().addListener((InvalidationListener)  e -> {
                blocksEditor().setChanged(true);
                resize();
            });

            listView.getItems().add("<编辑列表>");
        }
        return listView;
    }

    public void rowHeightChanged(int index, double height) {
        rowHeights.put(index,height);
        resize();
    }

    private void resize() {
        int defaultHeight = listView.getItems().size() - rowHeights.size();
        double computed = 0;
        for (Double height: rowHeights.values()) {
            computed = computed + height + 20;
        }
        double prefHeight = defaultHeight * ListBlockCell.height + computed;
        listView.setPrefHeight(prefHeight);
    }

    @Override
    public BlockData<List<String>> getData() {
        return new BlockData<>(
                this,
                Collections.unmodifiableList(
                        listView.getItems()
                )
        );
    }

    @Override
    protected String generate() {
        StringBuilder builder = new StringBuilder("\n");
        for (String line: listView.getItems()) {
            builder.append("\n - ").append(line);
        }
        return builder.toString();
    }

    @Override
    public void setData(BlockData data) {
        if (listView == null){
            getEditor();
        }
        listView.getItems().clear();
        rowHeights.clear();

        List<String> content = (List<String>) data.getContent();
        listView.getItems().addAll(content);
    }
}
