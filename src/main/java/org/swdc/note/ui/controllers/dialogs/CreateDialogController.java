package org.swdc.note.ui.controllers.dialogs;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.view.UIUtils;
import org.swdc.note.ui.view.dialogs.TypeCreateView;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateDialogController extends ViewController<TypeCreateView> {

    @Inject
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
        createView.hide();
    }

    @FXML
    public void onCreate() {
        if (txtName.getText().isBlank()) {
            return;
        }
        TypeCreateView createView = getView();
        ArticleType type = new ArticleType();
        type.setName(txtName.getText());
        type.setParent(createView.getParent());
        if((type = articleService.createType(type)) != null) {
            createView.hide();
            UIUtils.notification("分类《" + type.getName() + "》 保存成功！");
            //this.emit(new RefreshEvent(type,this, RefreshType.CREATION));
            txtName.setText("");
            return;
        }
        txtName.setText("");
    }

}
