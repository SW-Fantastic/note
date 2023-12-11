package org.swdc.note.ui.controllers.dialogs;

import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.files.StorageFactory;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.view.dialogs.TypeExportView;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TypeExportController extends ViewController<TypeExportView> {

    @FXML
    private ComboBox<StorageFactory> renderComboBox;

    @Inject
    private ArticleService articleService;

    private ObservableList<StorageFactory> renders = FXCollections.observableArrayList();

    private static class RenderConvertor extends StringConverter<StorageFactory> {

        private Map<String, StorageFactory> nameMap = new HashMap<>();

        public RenderConvertor(List<StorageFactory> exporters) {
            for (StorageFactory formatter: exporters) {
                if (nameMap.containsKey(formatter.getName())) {
                    continue;
                }
                nameMap.put(formatter.getName(), formatter);
            }
        }

        @Override
        public String toString(StorageFactory formatter) {
            return formatter == null ? null : formatter.getName();
        }

        @Override
        public StorageFactory fromString(String s) {
            return nameMap.get(s);
        }
    }

    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {

        renderComboBox.setItems(renders);


        List<StorageFactory> formatters = articleService.getAllExternalStorage(null);
        RenderConvertor convertor = new RenderConvertor(formatters);
        renderComboBox.setConverter(convertor);
        this.renders.addAll(formatters);
    }

    public StorageFactory getSelected(){
        return renderComboBox.getSelectionModel().getSelectedItem();
    }

    @FXML
    public void onOK() {
        TypeExportView exportView = getView();
        exportView.hide();
    }

    @FXML
    public void onCancel() {
        renderComboBox.getSelectionModel().clearSelection();
        TypeExportView exportView = getView();
        exportView.hide();
    }

}
