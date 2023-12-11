package org.swdc.note.ui.view.dialogs;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.AbstractSwingView;
import org.swdc.fx.view.View;


@View(viewLocation = "views/main/TrayPopupView.fxml")
public class TrayPopupView extends AbstractSwingView {

    @Inject
    private MaterialIconsService iconsService = null;

    @PostConstruct
    public void initialize() {
        this.initViewToolButton("mainView","home");
        this.initViewToolButton("create","edit");
        this.initViewToolButton("open","attach_file");
        getView().setOnMouseExited(e -> {
            this.hide();
        });
    }

    private void initViewToolButton(String id,String icon) {
        Button btn = findById(id);
        if (btn == null) {
            return;
        }
        btn.setPadding(new Insets(4,4,4,4));
        btn.setFont(iconsService.getFont(FontSize.MIDDLE));
        btn.setText(iconsService.getFontIcon(icon));
    }

}
