package org.swdc.note.ui.controllers.dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Listener;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;
import org.swdc.note.ui.view.UIUtils;
import org.swdc.note.ui.view.dialogs.TypeCreateView;
import org.swdc.note.ui.view.dialogs.TypeSelectView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static org.swdc.note.ui.view.UIUtils.findTypeItem;

public class TypeSelectController extends FXController {

    @FXML
    private TreeView<ArticleType> typeTree;

    @Aware
    private ArticleService service = null;

    private TreeItem<ArticleType> typeRoot = new TreeItem<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeTree.setShowRoot(false);
        typeTree.setRoot(typeRoot);
        typeRoot.setExpanded(true);
    }

    @Override
    public void initialize() {
        refresh(null);
    }

    @Listener(RefreshEvent.class)
    public void refresh(RefreshEvent event) {
        if (event == null || event.getData() == null) {
            typeRoot.getChildren().clear();
            List<ArticleType> types = this.service.getTypes();
            List<TreeItem<ArticleType>> items = types.stream()
                    .map(UIUtils::createTypeTree)
                    .collect(Collectors.toList());
            typeRoot.getChildren().addAll(items);
            return;
        }
        // 刷新分类树
        ArticleType type = event.getData();
        if (type.getParent() != null) {
            TreeItem<ArticleType> parent = findTypeItem(typeRoot,type.getParent());
            if (parent != null) {
                if (event.getType() == RefreshType.CREATION) {
                    parent.getChildren().add(new TreeItem<>(type));
                } else {
                    TreeItem<ArticleType> target = findTypeItem(typeRoot,type);
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
            TreeItem<ArticleType> target = findTypeItem(typeRoot,type);
            if (target == null) {
                typeRoot.getChildren().add(new TreeItem<>(type));
            } else {
                target.setValue(type);
            }
        }
    }


    @FXML
    private void createType() {
        TypeCreateView createView = findView(TypeCreateView.class);
        createView.show();
    }

    @FXML
    private void cancel() {
        typeTree.getSelectionModel().clearSelection();
        TypeSelectView selectView = getView();
        selectView.close();
    }

    @FXML
    private void ok() {
        TypeSelectView selectView = getView();
        selectView.close();
    }

}
