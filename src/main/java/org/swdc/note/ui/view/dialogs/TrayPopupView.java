package org.swdc.note.ui.view.dialogs;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import org.swdc.fx.PopupView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.MaterialIconsService;


@View
public class TrayPopupView extends PopupView {

    @Aware
    private MaterialIconsService iconsService = null;

    @Override
    public void initialize() {
        this.initViewToolButton("mainView","home");
        this.initViewToolButton("create","edit");
        this.initViewToolButton("open","attach_file");
        ((Parent)getView()).setOnMouseExited(e -> {
            this.close();
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
