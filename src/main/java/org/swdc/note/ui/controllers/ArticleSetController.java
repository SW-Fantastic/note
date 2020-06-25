package org.swdc.note.ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.render.HTMLResolver;
import org.swdc.note.core.service.ArticleService;

import java.net.URL;
import java.util.ResourceBundle;

public class ArticleSetController extends FXController {

    @FXML
    private ListView<Article> articles;

    @FXML
    private WebView contentView;

    @Aware
    private HTMLResolver render = null;

    @Aware
    private ArticleService articleService = null;


    private ObservableList<Article> articleList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        articles.setItems(articleList);
        articles.setOnMouseClicked(e -> this.onArticleRead());
    }

    /*public void onItemExport(ActionEvent event) {
        try {
            Article article = articles.getSelectionModel().getSelectedItem();
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(articleService.getExporterFilters(false));
            File file = chooser.showSaveDialog(null);
            if (file == null) {
                return;
            }
            FileExporter exporter = articleService.getFileExporter(file,false,true);
            exporter.writeFile(article, file.toPath());
        } catch (Exception e) {
            logger.error("fail to export selected item: ", e);
        }
    }

    public void onItemImport(ActionEvent event) {
        try {
            ArticleEditorView editorView = findView(ArticleEditorView.class);
            Article article = articles.getSelectionModel().getSelectedItem();
            if (article == null) {
                return;
            }

            editorView.addArticle(copyItem(article));
            editorView.show();
        } catch (Exception e) {
            logger.error("fail to load article for edit.",e);
        }
    } */

    private Article copyItem(Article article) throws Exception {
        Article articleEdit = new Article();
        articleEdit.setTitle(article.getTitle());
        articleEdit.setCreateDate(article.getCreateDate());
        articleEdit.setDesc(article.getDesc());
        ArticleContent content = new ArticleContent();
        ArticleContent original = article.getContent();
        content.setSource(original.getSource());
        content.setResources(original.getResources());
        content.setArticle(articleEdit);
        article.setContent(content);
        return article;
    }

    public synchronized void onArticleRead() {
        Article article = articles.getSelectionModel().getSelectedItem();
        if (article == null) {
            return;
        }
        try {
            String html = render.renderAsText(article);
            contentView.getEngine().loadContent(html);
        } catch (Exception e) {
            logger.error("fail to load content.");
        }
    }

    public void loadArticleType(ArticleType type) {
        articleList.addAll(type.getArticles());
    }

}
