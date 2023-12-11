package org.swdc.note.ui.view.cells;

import jakarta.annotation.PostConstruct;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.entities.Article;
import org.swdc.note.ui.view.ReaderView;

import java.text.SimpleDateFormat;

@View(stage = false,multiple = true,viewLocation = "views/main/ArticleSimpleCell.fxml")
public class ArticleSimpleCell extends AbstractView {

    private Article article;

    @PostConstruct
    public void initialize() {
        HBox view = (HBox) getView();
        view.setOnMouseClicked(this::onClick);
    }

    private void onClick(MouseEvent event) {
        if (article == null) {
            return;
        }
        if (event.getClickCount() >= 2) {
            ReaderView view = getView(ReaderView.class);
            view.addArticle(article);
            view.show();
        }
    }

    public void setArticle(Article article) {
        this.article = article;
        Label lblTitle = findById("title");
        Label lblDate = findById("date");
        lblTitle.setText(article.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(article.getCreateDate());
        lblDate.setText(date);
    }
}
