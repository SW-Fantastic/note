package org.swdc.note.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Listener;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.view.ArticleEditorView;
import org.swdc.note.ui.view.ReaderView;

import java.net.URL;
import java.util.ResourceBundle;

public class ReadViewController extends FXController {

    @Aware
    private ArticleService articleService = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Listener(RefreshEvent.class)
    public void onRefresh(RefreshEvent event) {
        if (event == null) {
            return;
        }
        Article article = event.getArticle();
        if (article == null) {
            return;
        }
        ReaderView readerView = getView();
        if (readerView.getArticle(article.getId()) == null) {
            if (readerView.getArticle(article.getLocation()) != null) {
                readerView.refresh(article.getLocation());
            }
            return;
        }
        readerView.refresh(article.getId());
    }

    @FXML
    public void editArticle() {
        ReaderView readerView = getView();
        Article article = readerView.getReadingArticle();
        if (article == null ||(article.getId() == null && article.getContentFormatter() == null)) {
            return;
        }
        ArticleEditorView editorView = findView(ArticleEditorView.class);
        editorView.addArticle(article);
        editorView.show();
    }

    @FXML
    public void deleteArticle() {
        ReaderView readerView = getView();
        Article article = readerView.getReadingArticle();
        if (article == null || article.getId() == null) {
            return;
        }
        readerView.showAlertDialog("删除","的确要删除《" + article.getTitle() + "》吗？", Alert.AlertType.CONFIRMATION)
                .ifPresent(btn -> {
                    if (btn == ButtonType.OK) {
                        ArticleType type = article.getType();
                        articleService.deleteArticle(article.getId());
                        readerView.closeTab(article.getId());
                        this.emit(new RefreshEvent(type,this));
                    }
                });
    }

    @FXML
    public void showArticles() {

    }

}
