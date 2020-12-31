package org.swdc.note.ui.view.cells;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Scope;
import org.swdc.fx.anno.ScopeType;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.MaterialIconsService;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;
import org.swdc.note.ui.view.ArticleEditorView;
import org.swdc.note.ui.view.ReaderView;

@Scope(ScopeType.MULTI)
@View(stage = false)
public class ArticleCell extends FXView {

    @Aware
    private ArticleEditorView editorView = null;

    @Aware
    private ReaderView readerView = null;

    @Aware
    private MaterialIconsService iconsService = null;

    @Aware
    private ArticleService articleService = null;

    private Article article;

    @Override
    public void initialize() {
        initViewToolButton("open","insert_drive_file", this::readArticle);
        initViewToolButton("edit","create", this::editArticle);
        initViewToolButton("delete","delete", this::deleteArticle);
        Parent cell = this.getView();
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
