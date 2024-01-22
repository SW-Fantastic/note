package org.swdc.note.ui.controllers.dialogs;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.CollectionType;
import org.swdc.note.core.service.CollectionService;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;
import org.swdc.note.ui.view.UIUtils;
import org.swdc.note.ui.view.dialogs.TypeCollectionEditView;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class TypeCollectionEditController extends ViewController<TypeCollectionEditView> {


    @FXML
    private TextField txtCollectName;

    @Inject
    private CollectionService collectionService;

    private CollectionType type;

    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void onCancel() {
        txtCollectName.setText("");
        TypeCollectionEditView editView = getView();
        editView.hide();
    }

    @FXML
    public void onSave() {

        if (txtCollectName.getText().isBlank()) {
            return;
        }

        TypeCollectionEditView view = getView();
        if (type == null) {
            CollectionType theType = new CollectionType();
            theType.setTitle(txtCollectName.getText());
            theType.setParent(view.getParent());
            if ((theType = collectionService.saveType(theType)) != null) {
                view.hide();
                UIUtils.notification("分类《" + theType.getTitle() + "》 保存成功！");
                txtCollectName.setText("");
                this.getView().emit(new RefreshEvent(
                        theType,
                        this,
                        RefreshType.CREATION
                ));
                return;
            }
            txtCollectName.setText("");
            return;
        }

        CollectionType exists = collectionService.getType(type.getId());
        exists.setTitle(txtCollectName.getText());
        exists.setDate(new Date());
        collectionService.saveType(exists);
        UIUtils.notification("分类《" + exists.getTitle() + "》 保存成功！");
        this.getView().emit(new RefreshEvent(
                exists,
                this,
                RefreshType.UPDATE
        ));
        txtCollectName.setText("");
        getView().hide();
    }

    public void setType(CollectionType type) {
        this.type = type;
        if (type == null) {
            txtCollectName.setText(null);
            return;
        }
        txtCollectName.setText(type.getTitle());
    }

}
