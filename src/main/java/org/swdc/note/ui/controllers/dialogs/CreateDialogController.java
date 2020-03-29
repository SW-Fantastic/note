package org.swdc.note.ui.controllers.dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.view.dialogs.TypeCreateView;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateDialogController extends FXController {

    @Aware
    private ArticleService articleService = null;

    @FXML
    private TextField txtName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void onCancel() {
        txtName.setText("");
        TypeCreateView createView = getView();
        createView.close();
    }

    @FXML
    public void onCreate() {
        if (txtName.getText().isBlank()) {
            return;
        }
        ArticleType type = new ArticleType();
        type.setName(txtName.getText());
        if(articleService.createType(type)) {
            TypeCreateView createView = getView();
            createView.close();
        }
        this.emit(new RefreshEvent((ArticleType) null,this));
        txtName.setText("");
    }

}
