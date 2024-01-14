package org.swdc.note.ui.component;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.controlsfx.control.PopOver;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.view.ReaderView;

import java.util.function.Consumer;

public class SearchPopover extends PopOver {

    private ListView<Article> articleListView;

    private ArticleService articleService;

    private Consumer<Article> clicked;

    public SearchPopover(ArticleService articleService) {
        this.articleListView = new ListView<>();
        this.articleListView.setMinHeight(120);
        this.articleListView.setMaxHeight(120);
        this.articleListView.getStyleClass().add("select-list");
        this.articleListView.setOnMouseClicked(e -> {
            Article article = articleListView.getSelectionModel().getSelectedItem();
            if (article != null) {
                clicked.accept(article);
                this.hide();
            }
        });
        this.setContentNode(this.articleListView);
        this.articleService = articleService;
    }

    public void search(String keywords) {
        ObservableList<Article> articles = this.articleListView.getItems();
        articles.clear();
        articles.addAll(
                articleService.searchByTitle(keywords)
        );
    }

    public void setClicked(Consumer<Article> clicked) {
        this.clicked = clicked;
    }
}
