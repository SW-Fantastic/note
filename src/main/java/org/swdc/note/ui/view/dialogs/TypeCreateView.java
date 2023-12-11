package org.swdc.note.ui.view.dialogs;

import jakarta.annotation.PostConstruct;
import javafx.scene.control.TextField;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.entities.ArticleType;

@View(title = "创建分类", dialog = true,viewLocation = "views/main/TypeCreateView.fxml")
public class TypeCreateView extends AbstractView {

    private ArticleType parent;

    @PostConstruct
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
