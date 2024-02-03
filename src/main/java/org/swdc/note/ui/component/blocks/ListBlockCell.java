package org.swdc.note.ui.component.blocks;

import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

import java.util.List;

public class ListBlockCell extends ListCell<String> {

    private TextField field;

    public static final double height = 36;

    private ListBlock block;

    public ListBlockCell(ListBlock listBlock) {
        this.block = listBlock;
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            if (field == null) {

                field = new TextField();
                Text textHolder = new Text();
                textHolder.textProperty().bind(field.textProperty());
                textHolder.fontProperty().bind(field.fontProperty());
                textHolder.layoutBoundsProperty().addListener(c -> {
                    double height = textHolder.getLayoutBounds().getHeight() + 18;
                    field.setPrefHeight(height);
                    block.rowHeightChanged(getIndex(),height);
                    block.blocksEditor().setChanged(true);
                });

                field.setPrefHeight(height);
                field.getStyleClass().add("field-list");
                field.textProperty().addListener(e -> {
                    textHolder.getLayoutBounds();
                    if (!field.getText().equals(getItem())) {
                        getListView().getItems().set(getIndex(),field.getText());
                    }
                });
                field.setOnKeyReleased(e -> {
                    if (e.getCode() == KeyCode.TAB || e.getCode() == KeyCode.ENTER) {
                        if (getIndex() == getListView().getItems().size() - 1) {
                            getListView().getItems().add("<编辑列表>");
                        }
                    }
                });
            }
            field.setText(item);
            setGraphic(field);
        }
    }
}
