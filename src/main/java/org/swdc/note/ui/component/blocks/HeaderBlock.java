package org.swdc.note.ui.component.blocks;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class HeaderBlock extends ArticleBlock {

    public static final String TYPE = "HEADER";

    private TextField textField;

    @Override
    public Node getEditor() {
        if (textField == null) {
            textField = new TextField();
            textField.setText("输入标题..");
            textField.getStyleClass().add("header-main");
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
        return new BlockData<>(
                this,textField.getText()
        );
    }

    @Override
    protected String generate() {
        return "# " + textField.getText();
    }

    @Override
    public void setData(BlockData data) {
        if (textField == null) {
            getEditor();
        }
        textField.setText(data.getContent().toString());
    }
}
