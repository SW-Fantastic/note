package org.swdc.note.ui.component.blocks;

import javafx.scene.Node;
import org.swdc.note.ui.component.ArticleBlocksEditor;

public abstract class ArticleBlock {


    private ArticleBlocksEditor editor;


    public void setEditor(ArticleBlocksEditor editor) {
        this.editor = editor;
    }

    protected String generate() {
        return "";
    }

    public Node getEditor() {
        return null;
    }

    protected ArticleBlocksEditor blocksEditor() {
        return editor;
    }

    public void remove() {
        if (editor.getBlocks().size() == 1) {
            return;
        }
        editor.getBlocks().remove(this);
    }

    public BlockData getData() {
        return null;
    }

    public void setData(BlockData data) {

    }
}
