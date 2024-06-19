package org.swdc.note.ui.component.blocks;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class ReferenceBlock extends ArticleBlock {

    private static final double lineHeight = 24;

    private TextArea textarea;

    @Override
    public Node getEditor() {
        if (textarea == null) {
            Text textHolder = new Text();
            textarea = new TextArea();
            textarea.setWrapText(true);
            textarea.getStyleClass().add("ref-text-area");
            textHolder.textProperty().bind(textarea.textProperty());
            textHolder.fontProperty().bind(textarea.fontProperty());
            textHolder.layoutBoundsProperty().addListener(c -> {
                textarea.setPrefHeight(textHolder.getLayoutBounds().getHeight() + 20);
            });

            textarea.setPrefHeight(lineHeight);
            textarea.setMinHeight(lineHeight);
            textarea.setText("输入文本...");
            textarea.textProperty().addListener(e -> {
                textHolder.getLayoutBounds();
                blocksEditor().setChanged(true);
                String text = textarea.getText();
                if (text.isEmpty()) {
                    remove();
                } else {
                    blocksEditor().doFocus(this);
                }
            });
        }
        return textarea;
    }

    @Override
    public BlockData<String> getData() {
        return new BlockData<>(
                this,
                textarea.getText()
        );
    }

    @Override
    protected String generate() {
        String[] lines = textarea.getText().split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line: lines) {
            sb.append("\n> ").append(line);
        }
        return sb.append("\n").toString();
    }

    @Override
    public void setData(BlockData data) {
        if (textarea == null) {
            getEditor();
        }
        textarea.setText(data.getContent().toString());
    }

}
