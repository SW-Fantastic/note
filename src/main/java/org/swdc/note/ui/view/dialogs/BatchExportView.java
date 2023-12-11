package org.swdc.note.ui.view.dialogs;

import jakarta.annotation.PostConstruct;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.files.SingleStorage;
import org.swdc.note.ui.controllers.dialogs.BatchExportViewController;

@View(title = "存储为",dialog = true,multiple = true,viewLocation = "views/main/BatchExportView.fxml")
public class BatchExportView extends AbstractView {

    @PostConstruct
    public void initialize() {
        getStage().setOnCloseRequest(e -> {
            BatchExportViewController controller = getController();
            controller.onCancel();
        });
    }

    public SingleStorage getSelected() {
        BatchExportViewController controller = getController();
        return controller.getSelected();
    }

}
