package org.swdc.note.ui.controllers.dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.note.core.files.SingleStorage;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.view.dialogs.BatchExportView;

import java.net.URL;
import java.util.ResourceBundle;

public class BatchExportViewController extends FXController {

    @FXML
    private ComboBox<SingleStorage> renderComboBox;

    @Aware
    private ArticleService service = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void initialize() {
        renderComboBox.getItems().addAll(service.getSingleStore(null));
    }

    public SingleStorage getSelected() {
        return renderComboBox.getSelectionModel().getSelectedItem();
    }

    @FXML
    public void onCancel() {
        renderComboBox.getSelectionModel().clearSelection();
        BatchExportView view = this.getView();
        view.close();
    }

    @FXML
    public void onOK() {
        BatchExportView view = this.getView();
        view.close();
    }

}
