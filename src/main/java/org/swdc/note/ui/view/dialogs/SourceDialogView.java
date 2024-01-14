package org.swdc.note.ui.view.dialogs;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.ui.controllers.dialogs.SourceDialogController;

@View(title = "载入",viewLocation = "views/dialogs/SourceDialogView.fxml")
public class SourceDialogView extends AbstractView {

    @Inject
    private MaterialIconsService iconsService;

    @PostConstruct
    public void initialize() {
        Button open = findById("btnOpen");
        open.setPadding(new Insets(4,4,4,4));
        open.setFont(iconsService.getFont(FontSize.MIDDLE_SMALL));
        open.setText(iconsService.getFontIcon("folder"));
        this.getStage().setOnCloseRequest(e -> {
            SourceDialogController controller = getController();
            controller.onCancel();
        });
    }

    public String getText() {
        SourceDialogController controller = getController();
        return controller.getURI();
    }

}
