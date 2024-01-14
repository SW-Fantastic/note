package org.swdc.note.ui.view.dialogs;

import jakarta.annotation.PostConstruct;
import javafx.scene.control.TextField;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.ui.controllers.dialogs.TypeEditController;

@View(dialog = true,title = "分类编辑",viewLocation = "views/dialogs/TypeEditView.fxml")
public class TypeEditView extends AbstractView {

    private ArticleType parent;


    @PostConstruct
    public void initialize() {
        this.getStage().setOnCloseRequest(e -> {
            TextField text = findById("txtName");
            text.setText("");
        });
    }

    public void setType(ArticleType type) {
        TypeEditController controller = getController();
        controller.setType(type);
    }

    public void setParent(ArticleType parent) {
        this.parent = parent;
    }

    public ArticleType getParent() {
        return parent;
    }
}
