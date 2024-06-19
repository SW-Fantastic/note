package org.swdc.note.ui.component.blocks;

import javafx.scene.Node;
import javafx.scene.layout.HBox;

/**
 * 一个特殊的block，应该永远处于Block列表的末尾。
 */
public class PlaceholderBlock extends ArticleBlock {

    private HBox box;


    @Override
    protected String generate() {
        return "";
    }

    @Override
    public Node getEditor() {
        if (box == null) {
            box = new HBox();
            box.setPrefWidth(0);
            box.setPrefHeight(0);
        }
        return box;
    }

    @Override
    public void setData(BlockData data) {

    }

    @Override
    public BlockData getData() {
        return new BlockData(this,"");
    }
}
