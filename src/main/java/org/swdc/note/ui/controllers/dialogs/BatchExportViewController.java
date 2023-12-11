package org.swdc.note.ui.controllers.dialogs;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.files.SingleStorage;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.view.dialogs.BatchExportView;

import java.net.URL;
import java.util.ResourceBundle;

public class BatchExportViewController extends ViewController<BatchExportView> {

    @FXML
    private ComboBox<SingleStorage> renderComboBox;

    @Inject
    private ArticleService service = null;

    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {
        renderComboBox.getItems().addAll(service.getSingleStore(null));
    }

    public SingleStorage getSelected() {
        return renderComboBox.getSelectionModel().getSelectedItem();
    }

    @FXML
    public void onCancel() {
        renderComboBox.getSelectionModel().clearSelection();
        BatchExportView view = this.getView();
        view.hide();
    }

    @FXML
    public void onOK() {
        BatchExportView view = this.getView();
        view.hide();
    }

}
