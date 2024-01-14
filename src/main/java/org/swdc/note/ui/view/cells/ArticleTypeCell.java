package org.swdc.note.ui.view.cells;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;
import org.swdc.note.ui.view.ArticleEditorView;
import org.swdc.note.ui.view.dialogs.TypeEditView;

import java.util.Date;

@View(stage = false,multiple = true,viewLocation = "views/cells/ArticleTypeCell.fxml")
public class ArticleTypeCell extends AbstractView {

    private ArticleType type;

    @Inject
    private MaterialIconsService iconsService = null;

    @Inject
    private ArticleService articleService = null;

    @Inject
    private ArticleEditorView editorView = null;

    @PostConstruct
    public void initialize() {
        initViewToolButton("add", "add", this::onArticleAdd);
        initViewToolButton("edit", "edit", this::onTypeEdit);
        initViewToolButton("delete", "delete", this::onTypeDelete);
        (this.getView()).setOnMouseClicked(this::onClick);
    }

    private void onClick(MouseEvent event) {
        if (type == null) {
            return;
        }
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() >= 2) {
            this.onArticleAdd(null);
        }
    }

    public void setType(ArticleType type) {
        this.type = type;
        Label name = findById("name");
        name.setText(type.getName());
    }

    public void updateArticle(String articleId) {
        Article article = articleService.getArticle(articleId);
        article.setType(this.type);
        articleService.saveArticle(article,article.getContent());
        this.emit(new RefreshEvent(this.type, this,RefreshType.UPDATE));
    }

    private void initViewToolButton(String id, String icon, EventHandler<ActionEvent> handler) {
        Button btn = findById(id);
        if (btn == null) {
            return;
        }
        btn.setPadding(new Insets(4,4,4,4));
        btn.setFont(iconsService.getFont(FontSize.SMALL));
        btn.setText(iconsService.getFontIcon(icon));
        btn.setOnAction(handler);
    }

    private void onArticleAdd(ActionEvent event) {
        editorView.show();
        Article article = new Article();
        article.setType(type);
        article.setContent(new ArticleContent());
        article.setCreateDate(new Date());
        article.setTitle("未命名");
        editorView.addArticle(article);
    }

    private void onTypeDelete(ActionEvent event) {
        articleService.deleteType(type);
       // this.emit(new RefreshEvent(type,this,RefreshType.DELETE));
    }

    private void onTypeEdit(ActionEvent event){
        TypeEditView editView = getView(TypeEditView.class);
        editView.setType(this.type);
        editView.show();
    }

}
