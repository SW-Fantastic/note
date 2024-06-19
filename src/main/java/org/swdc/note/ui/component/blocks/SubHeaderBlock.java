package org.swdc.note.ui.component.blocks;

import javafx.scene.Node;
import javafx.scene.control.TextField;

public class SubHeaderBlock extends ArticleBlock {

    private TextField textField;

    @Override
    public Node getEditor() {
        if (textField == null) {
            textField = new TextField();
            textField.setText("输入子标题..");
            textField.getStyleClass().add("header-sub");
            textField.textProperty().addListener(c -> {
                blocksEditor().setChanged(true);
                if (textField.getText().isEmpty()) {
                    remove();
                } else {
                    blocksEditor().doFocus(this);
                }
            });
        }
        return textField;
    }

    @Override
    public BlockData<String> getData() {
        return new BlockData<>(this,textField.getText());
    }

    @Override
    protected String generate() {
        return "## " + textField.getText();
    }

    @Override
    public void setData(BlockData data) {
        if (textField == null) {
            getEditor();
        }
        textField.setText(data.getContent().toString());
    }
}
