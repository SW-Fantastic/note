package org.swdc.note.ui.controllers.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import org.swdc.fx.FXController;
import org.swdc.note.core.render.FileExporter;
import org.swdc.note.ui.view.dialogs.TypeExportView;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TypeExportController extends FXController {

    @FXML
    private ComboBox<FileExporter> renderComboBox;

    private ObservableList<FileExporter> renders = FXCollections.observableArrayList();

    private static class RenderConvertor extends StringConverter<FileExporter> {

        private Map<String, FileExporter> nameMap = new HashMap<>();

        public RenderConvertor(List<FileExporter> exporters) {
            for (FileExporter exporter: exporters) {
                if (nameMap.containsKey(exporter.typeName())) {
                    continue;
                }
                nameMap.put(exporter.typeName(), exporter);
            }
        }

        @Override
        public String toString(FileExporter render) {
            return render == null ? null : render.typeName();
        }

        @Override
        public FileExporter fromString(String s) {
            return nameMap.get(s);
        }
    }

    @Override
    public void initialize() {
        List<FileExporter> exporters = getScoped(FileExporter.class);
        RenderConvertor convertor = new RenderConvertor(exporters);
        renderComboBox.setConverter(convertor);
        this.renders.addAll(exporters);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        renderComboBox.setItems(renders);
    }

    public FileExporter getSelected(){
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
