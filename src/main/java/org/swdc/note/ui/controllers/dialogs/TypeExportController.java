package org.swdc.note.ui.controllers.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import org.swdc.fx.FXController;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.formatter.ContentFormatter;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.view.dialogs.TypeExportView;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TypeExportController extends FXController {

    @FXML
    private ComboBox<ContentFormatter> renderComboBox;

    private ObservableList<ContentFormatter> renders = FXCollections.observableArrayList();

    private static class RenderConvertor extends StringConverter<ContentFormatter> {

        private Map<String, ContentFormatter> nameMap = new HashMap<>();

        public RenderConvertor(List<ContentFormatter> exporters) {
            for (ContentFormatter formatter: exporters) {
                if (nameMap.containsKey(formatter.getName())) {
                    continue;
                }
                nameMap.put(formatter.getName(), formatter);
            }
        }

        @Override
        public String toString(ContentFormatter formatter) {
            return formatter == null ? null : formatter.getName();
        }

        @Override
        public ContentFormatter fromString(String s) {
            return nameMap.get(s);
        }
    }

    @Override
    public void initialize() {
        ArticleService articleService = findService(ArticleService.class);
        List<ContentFormatter> formatters = articleService.getAllFormatter(item -> item.getType().equals(ArticleType.class)
                &&item.writeable());
        RenderConvertor convertor = new RenderConvertor(formatters);
        renderComboBox.setConverter(convertor);
        this.renders.addAll(formatters);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        renderComboBox.setItems(renders);
    }

    public ContentFormatter getSelected(){
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
