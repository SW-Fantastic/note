package org.swdc.note.ui.component.blocks;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import org.swdc.note.ui.component.MDRichTextUtils;

import java.time.Duration;

public class PlainTextBlock extends ArticleBlock {

    private static final double lineHeight = 24;

    private TextArea textarea;

    @Override
    public Node getEditor() {
        if (textarea == null) {
            Text textHolder = new Text();
            textarea = new TextArea();
            textarea.setWrapText(true);

            textHolder.textProperty().bind(textarea.textProperty());
            textHolder.fontProperty().bind(textarea.fontProperty());
            textHolder.wrappingWidthProperty().bind(textarea.widthProperty().subtract(40));
            textHolder.layoutBoundsProperty().addListener(c -> {
                textarea.setPrefHeight(textHolder.getLayoutBounds().getHeight() + 20);
            });

            textarea.setPrefHeight(lineHeight);
            textarea.setMinHeight(lineHeight);
            textarea.setText("输入文本...");
            textarea.textProperty().addListener(e -> {
                textHolder.getLayoutBounds();
                String text = textarea.getText();
                blocksEditor().setChanged(true);
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
        return new BlockData<>(this,textarea.getText());
    }

    @Override
    protected String generate() {
        return textarea.getText();
    }

    @Override
    public void setData(BlockData data) {
        if (textarea == null) {
            getEditor();
        }
        textarea.setText(data.getContent().toString());
    }
}
