package org.swdc.note.ui.view.dialogs;

import jakarta.annotation.PostConstruct;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.files.StorageFactory;
import org.swdc.note.ui.controllers.dialogs.TypeExportController;

@View(title = "导出", dialog = true,multiple = true,viewLocation = "views/dialogs/TypeExportView.fxml")
public class TypeExportView extends AbstractView {

    @PostConstruct
    public void initialize() {
        getStage().setOnCloseRequest(e -> {
            TypeExportController controller = getController();
            controller.onCancel();
        });
    }

    public StorageFactory getSelected() {
        TypeExportController controller = getController();
        return controller.getSelected();
    }

}
