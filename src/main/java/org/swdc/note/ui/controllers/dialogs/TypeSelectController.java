package org.swdc.note.ui.controllers.dialogs;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;
import org.swdc.note.ui.view.UIUtils;
import org.swdc.note.ui.view.dialogs.TypeEditView;
import org.swdc.note.ui.view.dialogs.TypeSelectView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static org.swdc.note.ui.view.UIUtils.findTypeItem;

public class TypeSelectController extends ViewController<TypeSelectView> {

    @FXML
    private TreeView<ArticleType> typeTree;

    @Inject
    private ArticleService service = null;

    private TreeItem<ArticleType> typeRoot = new TreeItem<>();

    @Override
    public void viewReady(URL url, ResourceBundle resourceBundle) {
        typeTree.setShowRoot(false);
        typeTree.setRoot(typeRoot);
        typeRoot.setExpanded(true);
        refresh(null);
    }


    @EventListener(type = RefreshEvent.class)
    public void refresh(RefreshEvent event) {
        if (event == null || event.getArticleType() == null) {
            typeRoot.getChildren().clear();
            List<ArticleType> types = this.service.getTypes();
            List<TreeItem<ArticleType>> items = types.stream()
                    .map(UIUtils::createTypeTree)
                    .collect(Collectors.toList());
            typeRoot.getChildren().addAll(items);
            return;
        }
        // 刷新分类树
        ArticleType type = event.getArticleType();
        if (type.getParent() != null) {
            TreeItem<ArticleType> parent = findTypeItem(typeRoot,type.getParent(),ArticleType::getId);
            if (parent != null) {
                if (event.getType() == RefreshType.CREATION) {
                    parent.getChildren().add(new TreeItem<>(type));
                } else {
                    TreeItem<ArticleType> target = findTypeItem(typeRoot,type,ArticleType::getId);
                    if (target == null) {
                        return;
                    }
                    if (event.getType() == RefreshType.DELETE) {
                        parent.getChildren().remove(target);
                    } else if (event.getType() == RefreshType.UPDATE) {
                        target.setValue(type);
                    }
                }
            }
        } else {
            TreeItem<ArticleType> target = findTypeItem(typeRoot,type,ArticleType::getId);
            if (target == null && event.getType() == RefreshType.CREATION) {
                typeRoot.getChildren().add(new TreeItem<>(type));
            } else if (event.getType() == RefreshType.UPDATE){
                target.setValue(type);
            } else if (event.getType() == RefreshType.DELETE) {
                typeRoot.getChildren().remove(target);
            }
        }
    }


    @FXML
    private void createType() {
        TypeEditView createView = getView().getView(TypeEditView.class);
        TreeItem<ArticleType> typeTreeItem = typeTree.getSelectionModel().getSelectedItem();
        createView.setParent(typeTreeItem == null ? null : typeTreeItem.getValue());
        createView.show();
    }

    @FXML
    private void cancel() {
        typeTree.getSelectionModel().clearSelection();
        TypeSelectView selectView = getView();
        selectView.hide();
    }

    @FXML
    private void ok() {
        TypeSelectView selectView = getView();
        selectView.hide();
    }

}
