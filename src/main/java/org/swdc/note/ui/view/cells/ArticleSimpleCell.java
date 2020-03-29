package org.swdc.note.ui.view.cells;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Scope;
import org.swdc.fx.anno.ScopeType;
import org.swdc.fx.anno.View;
import org.swdc.note.core.entities.Article;
import org.swdc.note.ui.view.ReaderView;

import java.text.SimpleDateFormat;

@Scope(ScopeType.MULTI)
@View(stage = false)
public class ArticleSimpleCell extends FXView {

    private Article article;

    @Override
    public void initialize() {
        HBox view = getView();
        view.setOnMouseClicked(this::onClick);
    }

    private void onClick(MouseEvent event) {
        if (article == null) {
            return;
        }
        if (event.getClickCount() >= 2) {
            ReaderView view = findView(ReaderView.class);
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
