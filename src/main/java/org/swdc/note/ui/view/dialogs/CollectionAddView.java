package org.swdc.note.ui.view.dialogs;

import javafx.stage.Stage;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.entities.CollectionType;
import org.swdc.note.ui.controllers.dialogs.CollectionAddViewController;

@View(title = "添加",dialog = true,resizeable = false,viewLocation = "views/dialogs/CollectAddDialog.fxml")
public class CollectionAddView extends AbstractView {

    public void show(CollectionType type) {
        if (type == null) {
            return;
        }
        Stage stage = getStage();
        CollectionAddViewController controller = getController();
        controller.setType(type);
        if (stage.isShowing()) {
            stage.toFront();
        } else {
            stage.showAndWait();
        }
    }

    @Override
    public void show() {
        throw new RuntimeException("invalid call, using another method instead.");
    }
}
