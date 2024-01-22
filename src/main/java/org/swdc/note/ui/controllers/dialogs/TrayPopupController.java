package org.swdc.note.ui.controllers.dialogs;

import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.ui.view.ArticleEditorView;
import org.swdc.note.ui.view.MainView;
import org.swdc.note.ui.view.dialogs.CollectionAddView;
import org.swdc.note.ui.view.dialogs.TrayPopupView;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class TrayPopupController extends ViewController<TrayPopupView> {

    @Inject
    private MainView mainView;

    @Inject
    private CollectionAddView collectionAddView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void exitApplication() {
        Platform.exit();
    }

    @FXML
    public void showMainView() {
        mainView.show();
        TrayPopupView popupView = getView();
        popupView.hide();
    }

    @FXML
    public void createNewDoc() {
        ArticleEditorView editorView = getView().getView(ArticleEditorView.class);
        Article article = new Article();
        article.setTitle("未命名" + new Date().getTime());
        article.setContent(new ArticleContent());
        article.setCreateDate(new Date());
        editorView.addArticle(article);
        editorView.show();
        TrayPopupView popupView = getView();
        popupView.hide();
    }

    @FXML
    public void openURL(){

        TrayPopupView popupView = getView();
        popupView.hide();
        collectionAddView.show();

    }

}
