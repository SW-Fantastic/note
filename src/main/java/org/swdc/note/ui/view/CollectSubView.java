package org.swdc.note.ui.view;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBase;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;

@View(viewLocation = "views/main/CollectSubView.fxml")
public class CollectSubView extends AbstractView {

    @Inject
    private MaterialIconsService iconsService;

    @PostConstruct
    public void init() {
        setupIcon(findById("addCollType"),"add");
    }

    private void setupIcon(ButtonBase buttonBase, String icon) {
        buttonBase.setFont(iconsService.getFont(FontSize.MIDDLE));
        buttonBase.setPadding(new Insets(4));
        buttonBase.setText(iconsService.getFontIcon(icon));
    }

}
