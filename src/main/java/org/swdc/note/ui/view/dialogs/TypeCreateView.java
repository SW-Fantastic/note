package org.swdc.note.ui.view.dialogs;

import javafx.scene.control.TextField;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.View;

@View(title = "创建分类", dialog = true)
public class TypeCreateView extends FXView {

    @Override
    public void initialize() {
        this.getStage().setOnCloseRequest(e -> {
            TextField text = findById("txtName");
            text.setText("");
        });
    }
}
