package org.swdc.note.ui.view.dialogs;

import org.swdc.fx.FXView;
import org.swdc.fx.anno.Scope;
import org.swdc.fx.anno.ScopeType;
import org.swdc.fx.anno.View;
import org.swdc.note.core.render.FileExporter;
import org.swdc.note.ui.controllers.dialogs.BatchExportViewController;

@Scope(ScopeType.MULTI)
@View(title = "存储为",dialog = true)
public class BatchExportView extends FXView {

    @Override
    public void initialize() {
        getStage().setOnCloseRequest(e -> {
            BatchExportViewController controller = getLoader().getController();
            controller.onCancel();
        });
    }

    public FileExporter getSelected() {
        BatchExportViewController controller = getLoader().getController();
        return controller.getSelected();
    }

}
