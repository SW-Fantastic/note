package org.swdc.note.ui.controllers.dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;
import org.swdc.note.ui.view.UIUtils;
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
        TypeCreateView createView = getView();
        ArticleType type = new ArticleType();
        type.setName(txtName.getText());
        type.setParent(createView.getParent());
        if((type = articleService.createType(type)) != null) {
            createView.close();
            UIUtils.notification("分类《" + type.getName() + "》 保存成功！",createView);
            //this.emit(new RefreshEvent(type,this, RefreshType.CREATION));
            txtName.setText("");
            return;
        }
        txtName.setText("");
    }

}
