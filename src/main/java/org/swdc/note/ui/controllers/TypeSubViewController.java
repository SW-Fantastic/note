package org.swdc.note.ui.controllers;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.swdc.fx.FXController;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Listener;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleType;
//import org.swdc.note.core.formatter.ContentFormatter;
import org.swdc.note.core.files.SingleStorage;
import org.swdc.note.core.files.StorageFactory;
import org.swdc.note.core.files.factory.AbstractStorageFactory;
import org.swdc.note.core.files.single.AbstractSingleStore;
import org.swdc.note.core.files.storages.AbstractArticleStorage;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;
import org.swdc.note.ui.view.*;
import org.swdc.note.ui.view.cells.*;
import org.swdc.note.ui.view.dialogs.BatchExportView;
import org.swdc.note.ui.view.dialogs.TypeCreateView;
import org.swdc.note.ui.view.dialogs.TypeEditView;
import org.swdc.note.ui.view.dialogs.TypeExportView;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.swdc.note.ui.view.UIUtils.findTypeItem;

public class TypeSubViewController extends FXController {

    @Aware
    private ArticleService articleService = null;

    @FXML
    private TreeView<ArticleType> typeTree;

    @FXML
    private ListView<Article> articlesList;

    @FXML
    private ListView<Article> recentlyList;

    @FXML
    private TextField txtSearch;

    @Aware
    private ReaderView readerView = null;

    @Aware
    private ArticleEditorView editorView = null;

    private ObservableList<Article> recently = FXCollections.observableArrayList();

    private ObservableList<Article> articles = FXCollections.observableArrayList();

    private TreeItem<ArticleType> typeRoot = new TreeItem<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeTree.setRoot(typeRoot);
        typeTree.setShowRoot(false);
        typeRoot.setExpanded(true);
    }

    @Override
    public void initialize() {
        typeTree.setOnMouseClicked(this::onTypeTreeClicked);
        typeTree.getSelectionModel()
                .selectedItemProperty()
                .addListener(this::onTreeSelectionChange);
        articlesList.setCellFactory(list -> new ArticleListCell(findView(ArticleCell.class)));
        articlesList.setItems(articles);
        articlesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        recentlyList.setCellFactory(list -> new ArticleSimpleListCell(findView(ArticleSimpleCell.class)));
        recentlyList.setItems(recently);
        this.refresh(null);
    }

    private void onTypeTreeClicked(MouseEvent e) {
        TreeItem<ArticleType> typeItem = typeTree.getSelectionModel().getSelectedItem();
        Object selectedView = e.getPickResult().getIntersectedNode();
        if (e.getPickResult().getIntersectedNode() instanceof TreeCell) {
            TreeCell<ArticleType> clickedItem = (TreeCell) selectedView;
            if (clickedItem.getItem() == null) {
                typeTree.getSelectionModel().clearSelection();
            }
        }
    }

    @Listener(RefreshEvent.class)
    public void refresh(RefreshEvent event) {
        recently.clear();
        recently.addAll(articleService.getRecently());

        if (event == null || event.getData() == null) {
            typeRoot.getChildren().clear();
            List<ArticleType> types = this.articleService.getTypes();
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
            if (target == null && event.getType() == RefreshType.CREATION) {
                typeRoot.getChildren().add(new TreeItem<>(type));
            } else if (event.getType() == RefreshType.UPDATE){
                target.setValue(type);
            } else if (event.getType() == RefreshType.DELETE) {
               typeRoot.getChildren().remove(target);
            }
        }
        // 刷新文档列表
        if (articles.size() > 0) {
            ArticleType current = articles.get(0).getType();
            if (type.getId().equals(current.getId())) {
                articles.clear();
                articles.addAll(articleService.getArticles(type));
                return;
            }
        }
        TreeItem<ArticleType> selected = typeTree.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getValue() == null) {
            return;
        }
        ArticleType selectedType = selected.getValue();
        if (type.getId().equals(selectedType.getId())) {
            articles.clear();
            articles.addAll(articleService.getArticles(type));
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
        TypeCreateView createView = findView(TypeCreateView.class);
        TreeItem<ArticleType> typeTreeItem = typeTree.getSelectionModel().getSelectedItem();
        if (typeTreeItem == null || typeTreeItem.getValue() == null) {
            createView.setParent(null);
        } else {
            createView.setParent(typeTreeItem.getValue());
        }
        createView.show();
    }

    @FXML
    public void onCreateDocument(ActionEvent event) {
       this.createNewDocument();
    }

    public void createNewDocument() {
        TreeItem<ArticleType> typeTreeItem = typeTree.getSelectionModel().getSelectedItem();

        ArticleEditorView editorView = findView(ArticleEditorView.class);
        Article article = new Article();
        article.setContent(new ArticleContent());
        article.setCreateDate(new Date());
        if (typeTreeItem != null && typeTreeItem.getValue() != null) {
            article.setType(typeTreeItem.getValue());
        }
        article.setTitle("未命名");
        editorView.addArticle(article);
        editorView.show();
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
        List<AbstractStorageFactory> factories = articleService.getAllExternalStorage(null);
        for (AbstractStorageFactory factory: factories) {
            if (!factory.support(file)) {
                continue;
            }
            // 加载数据
            ArticleSetView articleSetView = findView(ArticleSetView.class);
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
                            ReaderView readerView = findView(ReaderView.class);
                            readerView.addArticle(article);
                            readerView.show();
                        });
                    });
        }
    }

    @FXML
    public void showHelp() {
        File path = new File(new File(getAssetsPath()).getAbsolutePath() + "/help.noteset");
        AbstractStorageFactory storageFactory = articleService
                .getAllExternalStorage(f -> f.support(path)).get(0);
        ArticleSetView setView = findView(ArticleSetView.class);
        setView.loadContent(storageFactory,path);
        setView.show();
    }

    public void createDocument(ActionEvent event) {
        ArticleEditorView editorView = findView(ArticleEditorView.class);
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
        for (Article article: articles) {
            articleService.deleteArticle(article);
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
            UIUtils.notification("文档《" + article.getTitle() + "》已经导出。", this.getView());
        } else {
            BatchExportView batchExportView = findView(BatchExportView.class);
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
            UIUtils.notification("选择的文档已经导出。", this.getView());
        }
    }

    public void editArticle(ActionEvent event) {
        List<Article> articles = articlesList.getSelectionModel().getSelectedItems();
        if (articles == null || articles.isEmpty()) {
            return;
        }
        for (Article article: articles) {
            editorView.addArticle(article);
        }
        editorView.show();
    }

    public void creatType(ActionEvent event) {
        this.onTypeAdded();
    }

    public void deleteType(ActionEvent event) {
        FXView view = getView();
        TreeItem<ArticleType> type = typeTree.getSelectionModel().getSelectedItem();
        if (type == null || type.getValue() == null) {
            return;
        }
        ArticleType target = type.getValue();
        view.showAlertDialog("提示", "删除分类将会删除分类的全部文档，确定要这样做吗？", Alert.AlertType.CONFIRMATION)
                .ifPresent(btn -> {
                    if (btn == ButtonType.OK) {
                        articleService.deleteType(target);
                        this.emit(new RefreshEvent(target, view,RefreshType.DELETE));
                    }
                });
    }

    public void onModifyType(ActionEvent e) {
        TreeItem<ArticleType> type = typeTree.getSelectionModel().getSelectedItem();
        if (type == null || type.getValue() == null) {
            return;
        }
        TypeEditView editView = findView(TypeEditView.class);
        editView.setType(type.getValue());
        editView.show();
    }

    public void exportType(ActionEvent e) {
        TreeItem<ArticleType> typeItem = typeTree.getSelectionModel().getSelectedItem();
        if (typeItem == null || typeItem.getValue() == null) {
            return;
        }
        ArticleType type = typeItem.getValue();
        TypeExportView exportView = findView(TypeExportView.class);
        exportView.show();

        StorageFactory factory = exportView.getSelected();

        if (factory == null) {
            return;
        }
        AbstractArticleStorage storage = factory.getTypeStorage();
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

        UIUtils.notification("分类《" + type.getName() + "》已经导出。", this.getView());
    }

    @FXML
    public void onSearch() {
        if (txtSearch.getText().isBlank()) {
            return;
        }
        SearchView searchView = findView(SearchView.class);
        searchView.search(txtSearch.getText());
        searchView.show();
        txtSearch.clear();
    }

}
