package org.swdc.note.ui.view.cells;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.view.ArticleEditorView;
import org.swdc.note.ui.view.ReaderView;

@View(stage = false,multiple = true,viewLocation = "views/main/ArticleCell.fxml")
public class ArticleCell extends AbstractView {

    @Inject
    private ArticleEditorView editorView = null;

    @Inject
    private ReaderView readerView = null;

    @Inject
    private MaterialIconsService iconsService = null;

    @Inject
    private ArticleService articleService = null;

    private Article article;

    @PostConstruct
    public void initialize() {
        initViewToolButton("open","insert_drive_file", this::readArticle);
        initViewToolButton("edit","create", this::editArticle);
        initViewToolButton("delete","delete", this::deleteArticle);
        Parent cell = (Parent) this.getView();
        cell.setOnMouseClicked(this::onClick);
    }

    private void onClick(MouseEvent event) {
        if (article == null) {
            return;
        }
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() >= 2) {
            this.readArticle(null);
        }
    }

    private void deleteArticle(ActionEvent event) {
        articleService.deleteArticle(article);
        //this.emit(new RefreshEvent(article.getType(), this, RefreshType.DELETE));
    }

    private void editArticle(ActionEvent event) {
        editorView.show();
        editorView.addArticle(article);
    }

    private void readArticle(ActionEvent event) {
        readerView.addArticle(article);
        readerView.show();
    }

    public void setArticle(Article article) {
        this.article = article;
        if (article == null) {
            return;
        }
        Label title = findById("title");
        title.setText(article.getTitle());
        Label desc = findById("desc");
        desc.setText(article.getDesc());
    }

    private void initViewToolButton(String id, String icon, EventHandler<ActionEvent> handler) {
        Button btn = findById(id);
        if (btn == null) {
            return;
        }
        btn.setPadding(new Insets(4,4,4,4));
        btn.setFont(iconsService.getFont(FontSize.VERY_SMALL));
        btn.setText(iconsService.getFontIcon(icon));
        btn.setOnAction(handler);
    }
}
