package org.swdc.note.ui.controllers.dialogs;

import javafx.application.Platform;
import javafx.fxml.FXML;
import org.swdc.fx.FXController;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.ui.view.ArticleEditorView;
import org.swdc.note.ui.view.MainView;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class TrayPopupController extends FXController {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void exitApplication() {
        Platform.exit();
    }

    @FXML
    public void showMainView() {
        findView(MainView.class).show();
    }

    @FXML
    public void createNewDoc() {
        ArticleEditorView editorView = findView(ArticleEditorView.class);
        Article article = new Article();
        article.setTitle("未命名" + new Date().getTime());
        article.setContent(new ArticleContent());
        article.setCreateDate(new Date());
        editorView.addArticle(article);
        editorView.show();
    }

}
