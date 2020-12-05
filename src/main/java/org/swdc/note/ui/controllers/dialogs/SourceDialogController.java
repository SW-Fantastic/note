package org.swdc.note.ui.controllers.dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.proto.URLProtoResolver;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.view.ArticleEditorView;
import org.swdc.note.ui.view.ArticleSetView;
import org.swdc.note.ui.view.dialogs.SourceDialogView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SourceDialogController extends FXController {

    @FXML
    private TextField txtURI;

    @Aware
    private ArticleService service = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void onOK() {
        SourceDialogView view = getView();
        URLProtoResolver resolver = service.getURLResolver(txtURI.getText());
        if (resolver == null) {
            view.showAlertDialog("提示","不支持的url格式，无法打开", Alert.AlertType.ERROR);
            return;
        }
        Article art = resolver.resolveAsArticle(txtURI.getText());
        if (art != null) {
            ArticleEditorView editorView = findView(ArticleEditorView.class);
            editorView.addArticle(art);
            editorView.show();
            view.close();
            return;
        }

        ArticleType type = resolver.resolveAsArticleSet(txtURI.getText());
        if (type != null) {
            ArticleSetView articleSetView = findView(ArticleSetView.class);
            articleSetView.loadContent(type);
            articleSetView.show();
            view.close();
            return;
        }

        view.showAlertDialog("提示","暂不支持此格式，无法读取", Alert.AlertType.ERROR);
    }

    @FXML
    public void onCancel() {
        txtURI.setText("");
        SourceDialogView view = getView();
        view.close();
    }

    @FXML
    public void openFiles() {
        /*FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(service.getSupportedFilters(item -> item.readable()));
        chooser.setTitle("打开");
        File file = chooser.showOpenDialog(null);
        if (file == null) {
            return;
        } else {
            try {
                txtURI.setText(file.toURI().toURL().toExternalForm());
            } catch (Exception e) {
            }
        }*/
    }

    public String getURI() {
        return txtURI.getText();
    }

}
