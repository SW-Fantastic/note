package org.swdc.note.ui.component.blocks;

import javafx.scene.Node;
import javafx.scene.control.Separator;

public class SpreadBlock extends ArticleBlock {

    private Separator separator;

    @Override
    public Node getEditor() {
        if (separator == null) {
            separator = new Separator();
        }
        return separator;
    }

    @Override
    public BlockData<Boolean> getData() {
        return new BlockData<>(this,true);
    }

    @Override
    protected String generate() {
        return "- - -";
    }

    @Override
    public void setData(BlockData data) {
        if (separator == null) {
            getEditor();
        }
    }
}
