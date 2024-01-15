package org.swdc.note.ui.controllers;

import jakarta.inject.Inject;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.CollectionArticle;
import org.swdc.note.core.entities.CollectionType;
import org.swdc.note.core.service.CollectionService;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;
import org.swdc.note.ui.view.CollectSubView;
import org.swdc.note.ui.view.UIUtils;
import org.swdc.note.ui.view.dialogs.CollectionAddView;
import org.swdc.note.ui.view.dialogs.TypeCollectionEditView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CollectionSubViewController extends ViewController<CollectSubView> {


    @Inject
    private CollectionService collectionService;

    @Inject
    private CollectionAddView collectionAddView;

    @FXML
    private TreeView<CollectionType> collTypeTree;

    @FXML
    private TableView<CollectionArticle> articleTableView;

    @FXML
    private TableColumn<CollectionArticle,String> titleColumn;

    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {
        collTypeTree.setRoot(new TreeItem<>());
        collTypeTree.setShowRoot(false);
        collTypeTree.getSelectionModel()
                .selectedItemProperty()
                .addListener(this::selectedTypeChanged);

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        refresh(null);
        getView().disableTools(true);
    }

    private void selectedTypeChanged(ObservableValue observableValue, TreeItem<CollectionType> old, TreeItem<CollectionType> target) {

        ObservableList<CollectionArticle> collectionArticles = articleTableView.getItems();
        collectionArticles.clear();

        if (target == null || target.getValue() == null) {
            getView().disableTools(true);
            return;
        }

        getView().disableTools(false);
        collectionArticles.addAll(
                collectionService.getArticles(target.getValue().getId())
        );

    }


    @EventListener(type = RefreshEvent.class)
    public void refresh(RefreshEvent event) {

        if (event == null || event.getCollectionType() == null) {
            TreeItem<CollectionType> rootItem = collTypeTree.getRoot();
            List<CollectionType> typeList = collectionService.getCollectionTypes();
            List<TreeItem<CollectionType>> items = typeList
                    .stream()
                    .map(UIUtils::createCollectTypeTree)
                    .toList();
            ObservableList<TreeItem<CollectionType>> treeItems = rootItem.getChildren();
            treeItems.clear();
            treeItems.addAll(items);
            return;
        }

        CollectionType type = event.getCollectionType();
        CollectionType target = type.getParent();
        if (target != null) {
            // refresh the parent type
            TreeItem<CollectionType> parentTypeNode = UIUtils.findTypeItem(collTypeTree.getRoot(),type);
            if (parentTypeNode != null) {
                if (event.getType() == RefreshType.CREATION) {
                    parentTypeNode.getChildren()
                            .add(new TreeItem<>(type));
                } else {
                    TreeItem<CollectionType> theItem = UIUtils.findTypeItem(collTypeTree.getRoot(),type);
                    if (theItem == null) {
                        return;
                    }
                    if (event.getType() == RefreshType.DELETE) {
                        parentTypeNode.getChildren().remove(theItem);
                    } else if (event.getType() == RefreshType.UPDATE) {
                        theItem.setValue(type);
                    }
                }
            }
        }

    }

    @FXML
    public void onAddType() {
        CollectSubView subView = getView();
        TypeCollectionEditView editView = subView.getView(TypeCollectionEditView.class);
        TreeItem<CollectionType> type = collTypeTree.getSelectionModel()
                .getSelectedItem();
        if (type != null && type.getValue() != null) {
            editView.setParent(type.getValue());
        }
        editView.show();
    }

    @FXML
    public void onAddArticle() {
        TreeItem<CollectionType> typeTreeItem = collTypeTree.getSelectionModel().getSelectedItem();
        if (typeTreeItem == null || typeTreeItem.getValue() == null) {
            return;
        }
        collectionAddView.show(typeTreeItem.getValue());
    }

}
