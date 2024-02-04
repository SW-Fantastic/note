package org.swdc.note.ui.view.dialogs;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;

@View(dialog = true, title = "收集规则",viewLocation = "views/dialogs/CollectFocusDialog.fxml")
public class CollectionFocusView extends AbstractView {

    @Inject
    private MaterialIconsService iconsService;

    @PostConstruct
    public void init() {
        Stage stage = getStage();
        stage.setMinWidth(820);
        stage.setMinHeight(500);
        setupIcon(findById("add"),"add");
        setupIcon(findById("delete"),"delete");
    }

    private void setupIcon(ButtonBase buttonBase, String icon) {
        buttonBase.setFont(iconsService.getFont(FontSize.MIDDLE));
        buttonBase.setPadding(new Insets(4));
        buttonBase.setText(iconsService.getFontIcon(icon));
    }

    public void addable(boolean addable) {
        Button add = findById("add");
        add.setDisable(!addable);
    }

    public void removable(boolean removable) {
        Button remove = findById("delete");
        remove.setDisable(!removable);
    }

}
