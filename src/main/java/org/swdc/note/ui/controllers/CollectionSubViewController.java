package org.swdc.note.ui.controllers;

import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.CollectionArticle;
import org.swdc.note.core.entities.CollectionType;
import org.swdc.note.core.service.CollectionService;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;
import org.swdc.note.ui.view.CollectSubView;
import org.swdc.note.ui.view.CollectionReadView;
import org.swdc.note.ui.view.UIUtils;
import org.swdc.note.ui.view.cells.CollectTypeTreeCell;
import org.swdc.note.ui.view.dialogs.CollectionAddView;
import org.swdc.note.ui.view.dialogs.CollectionFocusView;
import org.swdc.note.ui.view.dialogs.TypeCollectionEditView;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class CollectionSubViewController extends ViewController<CollectSubView> {


    @Inject
    private CollectionService collectionService;

    @Inject
    private CollectionAddView collectionAddView;

    @Inject
    private CollectionReadView readView;

    @Inject
    private CollectionFocusView focusView;

    @FXML
    private TreeView<CollectionType> collTypeTree;

    @FXML
    private TableView<CollectionArticle> articleTableView;

    @FXML
    private TableColumn<CollectionArticle,String> titleColumn;

    @FXML
    private TableColumn<CollectionArticle, Date> dateColumn;

    private ContextMenu articleContextMenu;

    private ContextMenu typeContextMenu;

    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {

        collTypeTree.setRoot(new TreeItem<>());
        collTypeTree.setShowRoot(false);
        collTypeTree.setCellFactory(c -> new CollectTypeTreeCell(collectionService));
        collTypeTree.getSelectionModel()
                .selectedItemProperty()
                .addListener(this::selectedTypeChanged);

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        refresh(null);
        getView().disableTools(true);

        typeContextMenu = new ContextMenu();
        MenuItem itemAdd = new MenuItem("添加分类");
        itemAdd.setOnAction(this::onAddTypeMenu);

        MenuItem itemTrash = new MenuItem("删除..");
        itemTrash.setOnAction(this::onDeleteType);

        MenuItem itemRename = new MenuItem("重命名..");
        itemRename.setOnAction(this::onRenameTypeMenu);

        typeContextMenu.getItems().addAll(
                itemAdd,
                new SeparatorMenuItem(),
                itemTrash,
                itemRename
        );

        collTypeTree.setContextMenu(typeContextMenu);

        articleContextMenu = new ContextMenu();

        MenuItem itemArticleAdd = new MenuItem("抓取文档");
        itemArticleAdd.setOnAction(this::onAddArticle);

        MenuItem itemArticleDelete = new MenuItem("删除此文档");
        itemArticleDelete.setOnAction(this::onTrashArticle);

        articleContextMenu.getItems().addAll(
                itemArticleAdd,
                itemArticleDelete
        );
        articleTableView.setContextMenu(articleContextMenu);

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
                    .map(UIUtils::createTypeTree)
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
            TreeItem<CollectionType> parentTypeNode = UIUtils.findTypeItem(collTypeTree.getRoot(),target,CollectionType::getId);
            if (parentTypeNode != null) {
                if (event.getType() == RefreshType.CREATION) {
                    parentTypeNode.getChildren()
                            .add(new TreeItem<>(type));
                } else {
                    TreeItem<CollectionType> theItem = UIUtils.findTypeItem(collTypeTree.getRoot(),type,CollectionType::getId);
                    if (theItem == null) {
                        return;
                    }
                    if (event.getType() == RefreshType.DELETE) {
                        parentTypeNode
                                .getChildren()
                                .remove(theItem);
                    } else if (event.getType() == RefreshType.UPDATE) {
                        theItem.setValue(type);
                    }
                }
            }
        } else if (event.getType() == RefreshType.CREATION) {
            TreeItem<CollectionType> newType = new TreeItem<>(type);
            collTypeTree.getRoot()
                    .getChildren()
                    .add(newType);
        } else if (event.getType() == RefreshType.DELETE) {
            TreeItem<CollectionType> theItem = UIUtils.findTypeItem(collTypeTree.getRoot(),type,CollectionType::getId);
            if (theItem != null) {
                collTypeTree.getRoot()
                        .getChildren()
                        .remove(theItem);
            }
        } else if (event.getType() == RefreshType.UPDATE) {
            TreeItem<CollectionType> theItem = UIUtils.findTypeItem(collTypeTree.getRoot(),type,CollectionType::getId);
            if (theItem != null) {
                theItem.setValue(type);
            }
        }

        TreeItem<CollectionType> selected = collTypeTree.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getValue() == null) {
            return;
        }
        if (selected.getValue().getId().equals(type.getId())) {
            ObservableList<CollectionArticle> articles =  articleTableView.getItems();
            articles.clear();
            articles.addAll(
                    collectionService.getArticles(type.getId())
            );
        }
    }


    @FXML
    public void onTableClicked(MouseEvent mouseEvent) {

        if (mouseEvent.getClickCount() <= 1) {
            return;
        }

        CollectionArticle article = articleTableView.getSelectionModel()
                .getSelectedItem();

        if (article == null) {
            return;
        }

        readView.addArticle(article);
        readView.show();

    }

    @FXML
    public void onTableDragDec() {
        CollectionArticle article = articleTableView.getSelectionModel().getSelectedItem();
        if (article == null) {
            return;
        }
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.put(CollectTypeTreeCell.DATA_ARTICLE,article.getId());
        Dragboard dragboard = articleTableView.startDragAndDrop(TransferMode.MOVE);
        dragboard.setContent(clipboardContent);
    }


    @FXML
    public void onTableDragComplete(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasContent(CollectTypeTreeCell.DATA_ARTICLE)) {
            ObservableList<CollectionArticle> articles = articleTableView.getItems();
            TreeItem<CollectionType> articleTreeItem = collTypeTree.getSelectionModel().getSelectedItem();
            articles.clear();
            articles.addAll(collectionService.getArticles(articleTreeItem.getValue().getId()));
        }
    }

    private void onAddTypeMenu(ActionEvent event) {
        CollectSubView subView = getView();
        TypeCollectionEditView editView = subView.getView(TypeCollectionEditView.class);
        TreeItem<CollectionType> type = collTypeTree.getSelectionModel()
                .getSelectedItem();
        if (type != null && type.getValue() != null) {
            editView.setParent(type.getValue());
        }
        editView.setType(null);
        editView.show();
    }

    private void onRenameTypeMenu(ActionEvent event) {
        CollectSubView subView = getView();
        TypeCollectionEditView editView = subView.getView(TypeCollectionEditView.class);
        TreeItem<CollectionType> type = collTypeTree.getSelectionModel()
                .getSelectedItem();
        if (type != null && type.getValue() != null) {
            editView.setType(type.getValue());
            editView.show();
        }
    }

    private void onTrashArticle(ActionEvent event) {
        CollectionArticle article = articleTableView
                .getSelectionModel().getSelectedItem();
        if (article == null) {
            return;
        }
        collectionService.deleteCollectionArticle(article);
    }

    private void onAddArticle(ActionEvent event) {
        TreeItem<CollectionType> typeTreeItem = collTypeTree.getSelectionModel().getSelectedItem();
        if (typeTreeItem == null || typeTreeItem.getValue() == null) {
            return;
        }
        collectionAddView.show(typeTreeItem.getValue());
    }

    private void onDeleteType(ActionEvent e) {

        TreeItem<CollectionType> type = collTypeTree.getSelectionModel()
                .getSelectedItem();

        if (type == null || type.getValue() == null) {
            return;
        }

        CollectionType theType = type.getValue();

        Alert alert = getView().alert(
                "提示",
                "删除分类《" + theType.getTitle() + "》将会同时删除内部的子分类和全部收集内容，确定吗？",
                Alert.AlertType.CONFIRMATION
        );
        alert.showAndWait().ifPresent(act -> {
            if (act.getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
                collectionService.deleteCollectionType(theType);
            }
        });
    }

    @FXML
    public void onTreeDragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.MOVE);
    }

    @FXML
    public void onTreeDragDropped(DragEvent event) {

        event.setDropCompleted(true);

        Dragboard dragboard = event.getDragboard();
        Object dragged = dragboard.getContent(CollectTypeTreeCell.DATA_ARTICLE_TYPE);
        if (dragged == null) {
            return;
        }

        String typeId = dragged.toString();
        CollectionType type = collectionService.getType(typeId);

        TreeItem<CollectionType> parent = collTypeTree.getRoot();
        if (type.getParent() != null) {
            parent = UIUtils.findTypeItem(
                    collTypeTree.getRoot(),
                    type.getParent(),
                    CollectionType::getId
            );
        }

        type.setParent(null);
        type = collectionService.saveType(type);

        TreeItem<CollectionType> movedItem = UIUtils.findTypeItem(
                collTypeTree.getRoot(),
                type,
                CollectionType::getId
        );

        if (parent != null && movedItem != null) {
            parent.getChildren().remove(movedItem);
        }
        collTypeTree.getRoot().getChildren()
                .add(UIUtils.createTypeTree(type));
    }

    @FXML
    public void onAddType() {
        CollectSubView subView = getView();
        TypeCollectionEditView editView = subView.getView(TypeCollectionEditView.class);
        editView.setParent(null);
        editView.setType(null);
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

    @FXML
    public void onFocusViewShow() {
        if (focusView.getStage().isShowing()) {
            focusView.getStage().toFront();
        } else {
            focusView.show();
        }
    }

}
