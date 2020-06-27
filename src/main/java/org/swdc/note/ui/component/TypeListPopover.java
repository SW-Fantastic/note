package org.swdc.note.ui.component;

import javafx.scene.control.ListView;
import org.controlsfx.control.PopOver;
import org.swdc.note.core.entities.Article;

import java.util.Set;
import java.util.function.Consumer;

public class TypeListPopover extends PopOver {

    private ListView<Article> articles;
    private Consumer<Article> clicked;

    public TypeListPopover() {
        articles = new ListView<>();
        articles.setPrefHeight(280);
        articles.getStyleClass().add("select-list");
        this.setCloseButtonEnabled(true);
        this.setTitle("分类目录");
        this.setContentNode(articles);

        articles.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2) {
                if (clicked != null) {
                    clicked.accept(articles.getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    public void setArticles(Set<Article> articles){
        this.articles.getItems().clear();
        this.articles.getItems().addAll(articles);
    }

    public void onClick(Consumer<Article> clicked) {
        this.clicked = clicked;
    }

}
