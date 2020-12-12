package org.swdc.note.ui.controllers.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import org.swdc.fx.FXController;
import org.swdc.note.core.files.factory.AbstractStorageFactory;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.view.dialogs.TypeExportView;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TypeExportController extends FXController {

    @FXML
    private ComboBox<AbstractStorageFactory> renderComboBox;

    private ObservableList<AbstractStorageFactory> renders = FXCollections.observableArrayList();

    private static class RenderConvertor extends StringConverter<AbstractStorageFactory> {

        private Map<String, AbstractStorageFactory> nameMap = new HashMap<>();

        public RenderConvertor(List<AbstractStorageFactory> exporters) {
            for (AbstractStorageFactory formatter: exporters) {
                if (nameMap.containsKey(formatter.getName())) {
                    continue;
                }
                nameMap.put(formatter.getName(), formatter);
            }
        }

        @Override
        public String toString(AbstractStorageFactory formatter) {
            return formatter == null ? null : formatter.getName();
        }

        @Override
        public AbstractStorageFactory fromString(String s) {
            return nameMap.get(s);
        }
    }

    @Override
    public void initialize() {
        ArticleService articleService = findService(ArticleService.class);
        List<AbstractStorageFactory> formatters = articleService.getAllExternalStorage(null);
        RenderConvertor convertor = new RenderConvertor(formatters);
        renderComboBox.setConverter(convertor);
        this.renders.addAll(formatters);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        renderComboBox.setItems(renders);
    }

    public AbstractStorageFactory getSelected(){
        return renderComboBox.getSelectionModel().getSelectedItem();
    }

    @FXML
    public void onOK() {
        TypeExportView exportView = getView();
        exportView.close();
    }

    @FXML
    public void onCancel() {
        renderComboBox.getSelectionModel().clearSelection();
        TypeExportView exportView = getView();
        exportView.close();
    }

}
