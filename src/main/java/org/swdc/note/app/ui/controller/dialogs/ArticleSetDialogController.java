package org.swdc.note.app.ui.controller.dialogs;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.service.ArticleService;
import org.swdc.note.app.ui.view.MessageView;
import org.swdc.note.app.ui.view.dialogs.ArticleSetDialog;
import org.swdc.note.app.ui.view.dialogs.TypeDialog;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class ArticleSetDialogController implements Initializable {

    @Autowired
    private ArticleSetDialog dialog;

    @Autowired
    private ArticleService service;

    @Autowired
    private TypeDialog typeDialog;

    @Autowired
    private MessageView messageView;

    @FXML
    protected TreeView<ArticleType> typeTreeView;

    @FXML
    protected ListView<Article> articleList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeTreeView.getSelectionModel().selectedItemProperty().addListener(this::onSelectionChange);
        articleList.getSelectionModel().selectedItemProperty().addListener(this::onListSelectionChange);
    }

    @FXML
    private void singleImport() {
        Article article = articleList.getSelectionModel().getSelectedItem();

        if (article == null) {
            return;
        }

        Stage stage = typeDialog.getStage();
        stage.showAndWait();
        ArticleType type = typeDialog.getArticleType();
        if (type == null) {
            return;
        }

        Article created = new Article();
        created.setTitle(article.getTitle());
        created.setCreatedDate(article.getCreatedDate());
        created.setType(type);

        service.saveArticle(created, article.getContext());
        messageView.setMessage("文档：" + created.getTitle() + " 已经导入。");
        Notifications.create()
                .hideCloseButton()
                .graphic(messageView.getView())
                .position(Pos.CENTER)
                .owner(dialog.getStage())
                .hideAfter(new Duration(1200))
                .show();
    }

    private void onSelectionChange(Observable observable,TreeItem<ArticleType> oldVal, TreeItem<ArticleType> newVal) {
        if (newVal != null) {
            dialog.setArticles(newVal.getValue().getArticles());
        }
    }

    private void onListSelectionChange(Observable observable, Article oldVal, Article newVal) {
        if (newVal != null) {
            dialog.setContent(service.renderHTML(newVal.getContext()));
        }
    }

}
