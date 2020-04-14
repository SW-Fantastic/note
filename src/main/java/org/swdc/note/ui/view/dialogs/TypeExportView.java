package org.swdc.note.ui.view.dialogs;

import org.swdc.fx.FXView;
import org.swdc.fx.anno.Scope;
import org.swdc.fx.anno.ScopeType;
import org.swdc.fx.anno.View;
import org.swdc.note.core.render.FileExporter;
import org.swdc.note.ui.controllers.dialogs.TypeExportController;

@Scope(ScopeType.MULTI)
@View(title = "导出", dialog = true)
public class TypeExportView extends FXView {

    @Override
    public void initialize() {
        getStage().setOnCloseRequest(e -> {
            TypeExportController controller = getLoader().getController();
            controller.onCancel();
        });
    }

    public FileExporter getSelected() {
        TypeExportController controller = getLoader().getController();
        return controller.getSelected();
    }

}
