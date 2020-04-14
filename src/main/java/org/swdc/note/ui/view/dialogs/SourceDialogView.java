package org.swdc.note.ui.view.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.MaterialIconsService;
import org.swdc.note.ui.controllers.dialogs.SourceDialogController;

@View(title = "载入")
public class SourceDialogView extends FXView {

    @Aware
    private MaterialIconsService iconsService;

    @Override
    public void initialize() {
        Button open = findById("btnOpen");
        open.setPadding(new Insets(4,4,4,4));
        open.setFont(iconsService.getFont(FontSize.MIDDLE_SMALL));
        open.setText(iconsService.getFontIcon("folder"));
        this.getStage().setOnCloseRequest(e -> {
            SourceDialogController controller = getLoader().getController();
            controller.onCancel();
        });
    }

    public String getText() {
        SourceDialogController controller = getLoader().getController();
        return controller.getURI();
    }

}
