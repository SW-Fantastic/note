package org.swdc.note.ui.view.dialogs;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.entities.ArticleType;


@View(title = "分类", dialog = true,viewLocation = "views/main/TypeSelectView.fxml")
public class TypeSelectView extends AbstractView {

    @Inject
    private MaterialIconsService iconsService = null;

    @PostConstruct
    public void initialize() {
        Button add = findById("add");
        add.setFont(iconsService.getFont(FontSize.MIDDLE_SMALL));
        add.setText(iconsService.getFontIcon("add"));
        add.setPadding(new Insets(4,4,4,4));

        getStage().setOnCloseRequest(e -> {
            TreeView<ArticleType> typeListView = findById("typeTree");
            typeListView.getSelectionModel().clearSelection();
        });

    }

    public ArticleType getSelected() {
        TreeView<ArticleType> typeTree = findById("typeTree");
        TreeItem<ArticleType> type = typeTree.getSelectionModel().getSelectedItem();
        typeTree.getSelectionModel().clearSelection();
        if (type == null || type.getValue() == null) {
            return null;
        }
        return type.getValue();
    }

}
