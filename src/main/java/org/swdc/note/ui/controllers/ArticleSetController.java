package org.swdc.note.ui.controllers;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.files.ExternalStorage;
import org.swdc.note.core.files.StorageFactory;
import org.swdc.note.core.render.HTMLRender;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.view.ArticleSetView;
import org.swdc.note.ui.view.UIUtils;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ArticleSetController extends ViewController<ArticleSetView> {

    @FXML
    private TreeView<Object> typeTree;

    @FXML
    private WebView contentView;

    @Inject
    private HTMLRender render = null;

    @Inject
    private ArticleService articleService = null;

    private TreeItem<Object> root;

    private ExternalStorage storage = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root = new TreeItem<>();
        root.setExpanded(true);
        typeTree.setRoot(root);
        typeTree.setShowRoot(false);
        typeTree.setOnMouseClicked(this::onTreeItemClicked);
    }

    private Article copyItem(Article article) throws Exception {
        Article articleEdit = new Article();
        articleEdit.setTitle(article.getTitle());
        articleEdit.setCreateDate(article.getCreateDate());
        articleEdit.setDesc(article.getDesc());

        ArticleContent content = new ArticleContent();
        ArticleContent original = articleService.getContentOf(article);
        content.setSource(original.getSource());
        content.setImages(original.getImages());
        content.setArticleId(article.getId());

        articleEdit.setContent(content);

        return articleEdit;
    }

    public void onTreeItemClicked(MouseEvent event) {
        if (typeTree.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        TreeItem item = typeTree.getSelectionModel().getSelectedItem();
        if (item.getValue() == null) {
            return;
        }
        if (item.getValue() instanceof ArticleType) {
            return;
        }
        if (item.getValue() instanceof Article) {
            Article article = (Article)item.getValue();
            ArticleContent content = storage.getContent(article.getId());
            article.setContent(content);
            String html = render.renderAsText(article);
            contentView.getEngine().loadContent(html);
        }
    }

    public void loadContent(StorageFactory factory, File file)  {
        if (storage != null) {
            storage.close();
            storage = null;
        }
        storage = factory.getTypeStorage();
        if(!storage.open(file)) {
            storage = null;
            return;
        }
        List<TreeItem<Object>> types = storage.loadContents().stream()
                .map(UIUtils::createTypeTreeExternal)
                .collect(Collectors.toList());
        root.getChildren().clear();
        root.getChildren().addAll(types);
    }

    public void closeArticleSet() {
        if (storage != null) {
            storage.close();
            storage = null;
            root.getChildren().clear();
        }
    }

    @PreDestroy
    public void destroy() {
        this.closeArticleSet();
    }
}
