package org.swdc.note.ui.controllers;

import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.controlsfx.control.PopOver;
import org.slf4j.Logger;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.FXResources;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleEditorType;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.files.ExternalStorage;
import org.swdc.note.core.files.SingleStorage;
import org.swdc.note.core.files.StorageFactory;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.component.SearchPopover;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;
import org.swdc.note.ui.view.*;
import org.swdc.note.ui.view.cells.*;
import org.swdc.note.ui.view.dialogs.BatchExportView;
import org.swdc.note.ui.view.dialogs.TypeEditView;
import org.swdc.note.ui.view.dialogs.TypeExportView;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.swdc.note.ui.view.UIUtils.findTypeItem;

public class TypeSubViewController extends ViewController<TypeSubView> {

    @Inject
    private Logger logger;

    @Inject
    private FXResources resources;

    @Inject
    private ArticleService articleService = null;

    @FXML
    private TreeView<ArticleType> typeTree;

    @FXML
    private ListView<Article> articlesList;

    @FXML
    private ListView<Article> recentlyList;

    @FXML
    private TextField txtSearch;

    @Inject
    private ReaderView readerView = null;

    @Inject
    private ArticleEditorView editorView = null;

    @Inject
    private ArticleBlockEditorView blockEditorView = null;

    private ObservableList<Article> recently = FXCollections.observableArrayList();

    private ObservableList<Article> articles = FXCollections.observableArrayList();

    private TreeItem<ArticleType> typeRoot = new TreeItem<>();

    private SearchPopover searchPopover;

    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {
        typeTree.setRoot(typeRoot);
        typeTree.setShowRoot(false);
        typeRoot.setExpanded(true);

        typeTree.setOnMouseClicked(this::onTypeTreeClicked);
        typeTree.getSelectionModel()
                .selectedItemProperty()
                .addListener(this::onTreeSelectionChange);
        typeTree.setCellFactory(c -> new ArticleTypeTreeItem(articleService));

        articlesList.setCellFactory(list -> new ArticleListCell(getView().getView(ArticleCell.class)));
        articlesList.setItems(articles);
        articlesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        recentlyList.setCellFactory(list -> new ArticleSimpleListCell(getView().getView(ArticleSimpleCell.class)));
        recentlyList.setItems(recently);
        this.refresh(null);

        this.searchPopover = new SearchPopover(this.articleService);
        this.searchPopover.prefWidthProperty()
                .bind(txtSearch.widthProperty());
        this.searchPopover.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        this.searchPopover.setHideOnEscape(true);
        this.searchPopover.setClicked(art -> {
            txtSearch.clear();
            readerView.addArticle(art);
            readerView.show();
        });
    }

    private void onTypeTreeClicked(MouseEvent e) {
        Object selectedView = e.getPickResult().getIntersectedNode();
        if (e.getPickResult().getIntersectedNode() instanceof TreeCell) {
            TreeCell<ArticleType> clickedItem = (TreeCell) selectedView;
            if (clickedItem.getItem() == null) {
                typeTree.getSelectionModel().clearSelection();
            }
        }
    }

    @EventListener(type = RefreshEvent.class)
    public void refresh(RefreshEvent event) {
        recently.clear();
        recently.addAll(articleService.getRecently());

        if (event == null || event.getArticleType() == null) {
            typeRoot.getChildren().clear();
            List<ArticleType> types = this.articleService.getTypes();
            List<TreeItem<ArticleType>> items = types.stream()
                    .map(UIUtils::createTypeTree)
                    .toList();
            typeRoot.getChildren().addAll(items);
            return;
        }

        // 刷新分类树
        ArticleType type = event.getArticleType();
        Article article = event.getArticle();

        if (type.getParent() != null && article == null) {
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
        } else if (article == null) {
            TreeItem<ArticleType> target = findTypeItem(typeRoot,type,ArticleType::getId);
            if (target != null) {
                if (event.getType() == RefreshType.UPDATE){
                    target.setValue(type);
                } else if (event.getType() == RefreshType.DELETE) {
                    typeRoot.getChildren().remove(target);
                }
            } else if (event.getType() == RefreshType.CREATION) {
                typeRoot.getChildren().add(new TreeItem<>(type));
            }
        }
        // 刷新文档列表
        TreeItem<ArticleType> selected = typeTree.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getValue() == null) {
            return;
        }

        ArticleType current = selected.getValue();
        List<Article> articlesList = articleService.getArticles(current);
        if (type.getId().equals(current.getId()) || articles.size() != articlesList.size()) {
            articles.clear();
            articles.addAll(articlesList);
        }

    }

    private void onTreeSelectionChange(Observable observable, TreeItem<ArticleType> old, TreeItem<ArticleType> next) {
        if (next == null || next.getValue() == null) {
            return;
        }
        articles.clear();
        articles.addAll(articleService.getArticles(next.getValue()));
    }


    @FXML
    public void onTypeAdded() {
        TypeEditView createView = getView().getView(TypeEditView.class);
        TreeItem<ArticleType> typeTreeItem = typeTree.getSelectionModel().getSelectedItem();
        if (typeTreeItem == null || typeTreeItem.getValue() == null) {
            createView.setParent(null);
        } else {
            createView.setParent(typeTreeItem.getValue());
        }
        createView.show();
    }

    public void onCreateDocument(ActionEvent event) {
       this.createNewDocument(ArticleEditorType.MarkdownEditor);
    }

    public void onCreateBlockDocument(ActionEvent event) {
        createNewDocument(ArticleEditorType.BlockEditor);
    }


    public void createNewDocument(ArticleEditorType editorType) {
        TreeItem<ArticleType> typeTreeItem = typeTree.getSelectionModel().getSelectedItem();

        Article article = new Article();
        article.setContent(new ArticleContent());
        article.setCreateDate(new Date());
        if (typeTreeItem != null && typeTreeItem.getValue() != null) {
            article.setType(typeTreeItem.getValue());
        }
        article.setTitle("未命名");
        if (editorType == ArticleEditorType.BlockEditor) {
            ArticleBlockEditorView blockEditorView = getView().getView(ArticleBlockEditorView.class);
            blockEditorView.addArticle(article);
            blockEditorView.show();
        } else {
            ArticleEditorView editorView = getView().getView(ArticleEditorView.class);
            editorView.addArticle(article);
            editorView.show();
        }

    }

    @FXML
    public void onOpenFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("打开文档集");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("所有支持的格式","*.*"));
        File file = chooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        List<StorageFactory> factories = articleService.getAllExternalStorage(null);
        for (StorageFactory factory: factories) {
            if (!factory.support(file)) {
                continue;
            }
            // 加载数据
            ArticleSetView articleSetView = getView().getView(ArticleSetView.class);
            articleSetView.loadContent(factory,file);
            articleSetView.show();
            return;
        }
        List<SingleStorage> singleStores = articleService.getSingleStore(null);
        for (SingleStorage singleStorage: singleStores) {
            if (!singleStorage.support(file)) {
                continue;
            }
            CompletableFuture.supplyAsync(() -> singleStorage.load(file))
                    .whenCompleteAsync((article,e) -> {
                        if (e != null) {
                            logger.error("fail to load article " + file.getName());
                            return;
                        }
                        Platform.runLater(() -> {
                            ReaderView readerView = getView().getView(ReaderView.class);
                            readerView.addArticle(article);
                            readerView.show();
                        });
                    });
        }
    }

    @FXML
    public void showHelp() {

        File path = new File(resources.getAssetsFolder().getAbsolutePath() + "/help.noteset");
        StorageFactory storageFactory = articleService
                .getAllExternalStorage(f -> f.support(path)).get(0);
        ArticleSetView setView = getView().getView(ArticleSetView.class);
        setView.loadContent(storageFactory,path);
        setView.show();
    }

    public void createDocument(ActionEvent event) {
        ArticleEditorView editorView = getView().getView(ArticleEditorView.class);
        Article article = new Article();
        article.setContent(new ArticleContent());
        article.setCreateDate(new Date());
        article.setTitle("未命名");
        TreeItem<ArticleType> typeItem = typeTree.getSelectionModel().getSelectedItem();
        ArticleType type = null;
        if (typeItem == null) {
            return;
        }
        type = typeItem.getValue();
        if (type != null) {
            article.setType(type);
        }
        editorView.addArticle(article);
        editorView.show();
    }

    public void openArticle(ActionEvent event) {
        List<Article> articles = articlesList.getSelectionModel().getSelectedItems();
        if (articles == null || articles.isEmpty()) {
            return;
        }
        for (Article article: articles) {
            readerView.addArticle(article);
        }
        readerView.show();
    }

    public void deleteArticle(ActionEvent event) {
        List<Article> articles = articlesList.getSelectionModel().getSelectedItems();
        if (articles == null || articles.isEmpty()) {
            return;
        }
        for (int idx = 0; idx < articles.size();idx ++) {
            articleService.deleteArticle(articles.get(idx));
        }
    }

    public void exportArticle(ActionEvent event) {
        List<Article> articles = articlesList.getSelectionModel().getSelectedItems();
        if (articles == null || articles.isEmpty()) {
            return;
        }
        if (articles.size() == 1) {
            List<SingleStorage> storageList = articleService.getSingleStore(null);
            Map<FileChooser.ExtensionFilter,SingleStorage> filterStorageMap = new HashMap<>();
            for (SingleStorage singleStorage: storageList) {
                filterStorageMap.put(singleStorage.getFilter(),singleStorage);
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("另存为");
            fileChooser.getExtensionFilters()
                    .addAll(filterStorageMap.keySet());
            File file = fileChooser.showSaveDialog(null);
            if (file == null) {
                return;
            }
            Article article = articlesList.getSelectionModel().getSelectedItem();
            if (article == null) {
                return;
            }
            SingleStorage singleStorage = filterStorageMap.get(fileChooser.getSelectedExtensionFilter());
            singleStorage.save(article,file);
            UIUtils.notification("文档《" + article.getTitle() + "》已经导出。");
        } else {
            BatchExportView batchExportView = getView().getView(BatchExportView.class);
            batchExportView.show();
            SingleStorage store = batchExportView.getSelected();
            if (store == null) {
                return;
            }
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("另存为");
            File directory = directoryChooser.showDialog(null);
            if (directory == null) {
                return;
            }
            Path dir = directory.toPath().toAbsolutePath();
            for (Article article: articles) {
                Path path = dir.resolve(article.getTitle() + "." + store.getExtension());
                store.save(article,path.toFile());
            }
            UIUtils.notification("选择的文档已经导出。");
        }
    }

    public void editArticle(ActionEvent event) {
        List<Article> articles = articlesList.getSelectionModel().getSelectedItems();
        if (articles == null || articles.isEmpty()) {
            return;
        }
        for (Article article: articles) {
            if (article.getEditorType() == ArticleEditorType.BlockEditor) {
                blockEditorView.addArticle(article);
                blockEditorView.show();
            } else {
                editorView.addArticle(article);
                editorView.show();
            }
        }
    }

    public void creatType(ActionEvent event) {
        this.onTypeAdded();
    }

    public void deleteType(ActionEvent event) {
        AbstractView view = getView();
        TreeItem<ArticleType> type = typeTree.getSelectionModel().getSelectedItem();
        if (type == null || type.getValue() == null) {
            return;
        }
        ArticleType target = type.getValue();
        view.alert("提示", "删除分类将会删除分类的全部文档，确定要这样做吗？", Alert.AlertType.CONFIRMATION)
                .showAndWait()
                .ifPresent(btn -> {
                    if (btn == ButtonType.OK) {
                        articleService.deleteType(target);
                        this.getView().emit(new RefreshEvent(target, view,RefreshType.DELETE));
                    }
                });
    }

    public void onModifyType(ActionEvent e) {
        TreeItem<ArticleType> type = typeTree.getSelectionModel().getSelectedItem();
        if (type == null || type.getValue() == null) {
            return;
        }
        TypeEditView editView = getView().getView(TypeEditView.class);
        editView.setType(type.getValue());
        editView.show();
    }

    public void exportType(ActionEvent e) {
        TreeItem<ArticleType> typeItem = typeTree.getSelectionModel().getSelectedItem();
        if (typeItem == null || typeItem.getValue() == null) {
            return;
        }
        ArticleType type = typeItem.getValue();
        TypeExportView exportView = getView().getView(TypeExportView.class);
        exportView.show();

        StorageFactory factory = exportView.getSelected();

        if (factory == null) {
            return;
        }
        ExternalStorage storage = factory.getTypeStorage();
        if (storage == null) {
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("导出");
        chooser.getExtensionFilters().add(storage.getFilter());
        chooser.setInitialFileName(type.getName());
        File target = chooser.showSaveDialog(null);
        if (target == null) {
            return;
        }

        storage.open(target);

        type = articleService.getType(type.getId());
        storage.addType(type);
        storage.close();

        UIUtils.notification("分类《" + type.getName() + "》已经导出。");
    }

    @FXML
    public void onSearch() {
        if (txtSearch.getText().isBlank()) {
            return;
        }
        searchPopover.search(txtSearch.getText());
        searchPopover.show(txtSearch);
    }

    @FXML
    public void onTreeDragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.MOVE);
    }

    @FXML
    public void onTreeDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasContent(ArticleTypeTreeItem.DATA_EDIT_TYPE)) {

            String movedTypeId = dragboard.getContent(ArticleTypeTreeItem.DATA_EDIT_TYPE).toString();
            ArticleType moved = articleService.getType(movedTypeId);

            TreeItem<ArticleType> parentItem = typeTree.getRoot();
            if (moved.getParent() != null) {
                parentItem = UIUtils.findTypeItem(
                        typeTree.getRoot(),
                        moved.getParent(),
                        ArticleType::getId
                );
            }

            TreeItem<ArticleType> movedItem = UIUtils.findTypeItem(
                    typeTree.getRoot(),
                    moved,
                    ArticleType::getId
            );

            if (parentItem != null && movedItem != null) {
                parentItem.getChildren().remove(movedItem);
            }

            moved.setParent(null);
            moved = articleService.saveType(moved);

            typeTree.getRoot().getChildren()
                    .add(UIUtils.createTypeTree(moved));

            event.setDropCompleted(true);
        }
    }

}
