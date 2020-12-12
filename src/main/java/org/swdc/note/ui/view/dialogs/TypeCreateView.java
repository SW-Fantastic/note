package org.swdc.note.ui.view.dialogs;

import javafx.scene.control.TextField;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.View;
import org.swdc.note.core.entities.ArticleType;

@View(title = "创建分类", dialog = true)
public class TypeCreateView extends FXView {

    private ArticleType parent;

    @Override
    public void initialize() {
        this.getStage().setOnCloseRequest(e -> {
            TextField text = findById("txtName");
            text.setText("");
        });
    }

    public void setParent(ArticleType parent) {
        this.parent = parent;
    }

    public ArticleType getParent() {
        return parent;
    }
}
