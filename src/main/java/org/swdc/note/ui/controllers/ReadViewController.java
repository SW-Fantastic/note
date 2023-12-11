package org.swdc.note.ui.controllers;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.component.TypeListPopover;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;
import org.swdc.note.ui.view.ArticleEditorView;
import org.swdc.note.ui.view.ReaderView;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

public class ReadViewController extends ViewController<ReaderView> {

    @Inject
    private ArticleService articleService = null;

    @Inject
    private ArticleEditorView articleEditorView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @EventListener(type = RefreshEvent.class)
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
            if (readerView.getArticle(article.getFullPath()) != null) {
                readerView.refreshByExternal(article);
            }
            return;
        }
        readerView.refresh(article.getId());
    }

    @FXML
    public void editArticle() {
        ReaderView readerView = getView();
        Article article = readerView.getReadingArticle();
        if (article == null ||(article.getId() == null && article.getSingleStore() == null)) {
            return;
        }
        ArticleEditorView editorView = getView().getView(ArticleEditorView.class);
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
        readerView.alert("删除","的确要删除《" + article.getTitle() + "》吗？", Alert.AlertType.CONFIRMATION)
                .showAndWait()
                .ifPresent(btn -> {
                    if (btn == ButtonType.OK) {
                        ArticleType type = article.getType();
                        articleService.deleteArticle(article);
                        readerView.closeTab(article.getId());
                        readerView.emit(new RefreshEvent(type,this.getView(), RefreshType.DELETE));
                    }
                });
    }

    @FXML
    public void showArticles() {
        ReaderView readerView = getView();
        Article article = readerView.getReadingArticle();
        TypeListPopover popover = readerView.getPopover();
        ArticleType type = article.getType();
        if (type == null) {
            popover.setArticles(Collections.emptySet());
        } else {
            type = articleService.getType(type.getId());
            popover.setArticles(type.getArticles());
        }
        Button typeButton = readerView.findById("toc");
        popover.show(typeButton);
    }

}
