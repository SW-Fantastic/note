package org.swdc.note.ui.view.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.MaterialIconsService;
import org.swdc.note.core.entities.ArticleType;


@View(title = "分类", dialog = true)
public class TypeSelectView extends FXView {

    @Aware
    private MaterialIconsService iconsService = null;

    @Override
    public void initialize() {
        Button add = findById("add");
        add.setFont(iconsService.getFont(FontSize.MIDDLE_SMALL));
        add.setText(iconsService.getFontIcon("add"));
        add.setPadding(new Insets(4,4,4,4));

        getStage().setOnCloseRequest(e -> {
            ListView<ArticleType> typeListView = findById("typelist");
            typeListView.getSelectionModel().clearSelection();
        });

    }

    public ArticleType getSelected() {
        ListView<ArticleType> typeListView = findById("typelist");
        ArticleType type = typeListView.getSelectionModel().getSelectedItem();
        typeListView.getSelectionModel().clearSelection();
        return type;
    }

}
