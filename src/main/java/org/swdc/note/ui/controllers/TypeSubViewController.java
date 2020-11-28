package org.swdc.note.ui.controllers;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.swdc.fx.FXController;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Listener;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.formatter.ContentFormatter;
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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TypeSubViewController extends FXController {

    @Aware
    private ArticleService articleService = null;

   // @FXML
   // private ListView<ArticleType> typeList;

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
        typeTree.setOnMouseClicked(e -> {
            if (e.getClickCount() > 1){
                typeTree.getSelectionModel().clearSelection();
            }
        });
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

    @Listener(RefreshEvent.class)
    public void refresh(RefreshEvent event) {
        recently.clear();
        recently.addAll(articleService.getRecently());

        if (event == null || event.getData() == null) {
            typeRoot.getChildren().clear();
            List<ArticleType> types = this.articleService.getTypes();
            List<TreeItem<ArticleType>> items = types.stream()
                    .map(this::createTypeTree)
                    .collect(Collectors.toList());
            typeRoot.getChildren().addAll(items);
            return;
        }

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

    private void onTreeSelectionChange(Observable observable, TreeItem<ArticleType> old, TreeItem<ArticleType> next) {
        if (next == null || next.getValue() == null) {
            return;
        }
        articles.clear();
        articles.addAll(articleService.getArticles(next.getValue()));
    }

    private TreeItem<ArticleType> findTypeItem(TreeItem<ArticleType> typeNode,ArticleType type) {
        if (typeNode.getValue() != null && typeNode.getValue().getId().equals(type.getId())) {
            return typeNode;
        }
        if (typeNode.getChildren().size() > 0) {
            for (TreeItem<ArticleType> item: typeNode.getChildren()) {
                if (item.getValue().getId().equals(type.getId())) {
                    return item;
                } else if (item.getChildren().size() > 0){
                    return findTypeItem(item,type);
                }
            }
        }
        return null;
    }

    private TreeItem<ArticleType> createTypeTree(ArticleType type) {
        TreeItem<ArticleType> item = new TreeItem<>(type);
        if (type.getChildren().size() > 0) {
            for (ArticleType subType: type.getChildren()) {
                TreeItem<ArticleType> subItem = createTypeTree(subType);
                item.getChildren().add(subItem);
            }
        }
        return item;
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
    public void onCreateDocument() {
        ArticleEditorView editorView = findView(ArticleEditorView.class);
        Article article = new Article();
        article.setContent(new ArticleContent());
        article.setCreateDate(new Date());
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
        // 从exporter读取，存在支持的exporter那么就读进来
        // 按照分类和文档两种模式获取和处理
        ContentFormatter formatter = articleService.getFormatter(file,Article.class);
        if (formatter != null) {
            if (!formatter.readable()) {
                return;
            }
            Article article = (Article) formatter.load(file.toPath());
            ReaderView readerView = findView(ReaderView.class);
            readerView.addArticle(article);
            readerView.show();
        } else {
            formatter = articleService.getFormatter(file,ArticleType.class);
            if (formatter == null || !formatter.readable()) {
                // 不支持
                return;
            }
            ContentFormatter contentFormatter = formatter;
            CompletableFuture.supplyAsync(() -> contentFormatter.load(file.toPath()))
                    .whenCompleteAsync((type,e) -> {
                        if (e != null) {
                            logger.error("fail to load article set: " + contentFormatter.getExtension());
                            return;
                        }
                        Platform.runLater(() -> {
                            ArticleSetView articleSetView = findView(ArticleSetView.class);
                            articleSetView.loadContent((ArticleType) type);
                            articleSetView.show();
                        });
                    });
        }
    }

    @FXML
    public void showHelp() {
        File path = new File(new File(getAssetsPath()).getAbsolutePath() + "/help.mdsrc");
        ContentFormatter<ArticleType> exporter = articleService.getFormatter(path,ArticleType.class);
        ArticleType type = exporter.load(path.toPath());
        ArticleSetView setView = findView(ArticleSetView.class);
        setView.loadContent(type);
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
            ArticleType type = article.getType();
            articleService.deleteArticle(article);
           // this.emit(new RefreshEvent(type, this.getView(), RefreshType.DELETE));
        }
    }

    public void exportArticle(ActionEvent event) {
        List<Article> articles = articlesList.getSelectionModel().getSelectedItems();
        if (articles == null || articles.isEmpty()) {
            return;
        }
        if (articles.size() == 1) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("另存为");
            fileChooser.getExtensionFilters()
                    .addAll(articleService.getSupportedFilters(item -> item.getType().equals(Article.class) && item.writeable()));
            File file = fileChooser.showSaveDialog(null);
            if (file == null) {
                return;
            }
            ContentFormatter formatter = articleService.getFormatter(file,Article.class);
            if (formatter == null || !formatter.writeable()) {
                return;
            }
            Article article = articlesList.getSelectionModel().getSelectedItem();
            if (article == null) {
                return;
            }
           formatter.save(file.toPath(),article);
            UIUtils.notification("文档《" + article.getTitle() + "》已经导出。", this.getView());
        } else {
            BatchExportView batchExportView = findView(BatchExportView.class);
            batchExportView.show();
            ContentFormatter formatter = batchExportView.getSelected();
            if (formatter == null) {
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
               Path path = dir.resolve(article.getTitle() + "." + formatter.getExtension());
               formatter.save(path,article);
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
        /*ArticleType type = typeList.getSelectionModel().getSelectedItem();
        if (type == null) {
            return;
        }
        TypeExportView exportView = findView(TypeExportView.class);
        exportView.show();
        ContentFormatter formatter = exportView.getSelected();
        if(formatter == null || !formatter.writeable()) {
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("导出");
        chooser.getExtensionFilters().add(formatter.getExtensionFilter());
        chooser.setInitialFileName(type.getName());
        File target = chooser.showSaveDialog(null);
        if (target == null) {
            return;
        }
        type = articleService.getType(type.getId());
        formatter.save(target.toPath(),type);
        UIUtils.notification("分类《" + type.getName() + "》已经导出。", this.getView());*/
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
