package org.swdc.note.ui.view.cells;

import org.swdc.note.core.entities.ArticleType;

public class ArticleTypeTreeItem {

    private ArticleType type;

    public ArticleTypeTreeItem(ArticleType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type.getName();
    }
}
