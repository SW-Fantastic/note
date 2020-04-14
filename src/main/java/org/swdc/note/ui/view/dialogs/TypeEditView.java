package org.swdc.note.ui.view.dialogs;

import javafx.scene.control.TextField;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.View;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.ui.controllers.dialogs.TypeEditController;

@View(dialog = true,title = "修改分类")
public class TypeEditView extends FXView {

    @Override
    public void initialize() {
        this.getStage().setOnCloseRequest(e -> {
            TextField text = findById("txtName");
            text.setText("");
        });
    }

    public void setType(ArticleType type) {
        TypeEditController controller = getLoader().getController();
        controller.setType(type);
    }

}
