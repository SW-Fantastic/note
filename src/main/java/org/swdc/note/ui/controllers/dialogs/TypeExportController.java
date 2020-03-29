package org.swdc.note.ui.controllers.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import org.swdc.fx.FXController;
import org.swdc.note.core.render.ContentRender;
import org.swdc.note.ui.view.dialogs.TypeExportView;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TypeExportController extends FXController {

    @FXML
    private ComboBox<ContentRender> renderComboBox;

    private ObservableList<ContentRender> renders = FXCollections.observableArrayList();

    private static class RenderConvertor extends StringConverter<ContentRender> {

        private Map<String, ContentRender> nameMap = new HashMap<>();

        public RenderConvertor(List<ContentRender> renders) {
            for (ContentRender render: renders) {
                if (nameMap.containsKey(render.typeName())) {
                    continue;
                }
                nameMap.put(render.typeName(), render);
            }
        }

        @Override
        public String toString(ContentRender render) {
            return render == null ? null : render.typeName();
        }

        @Override
        public ContentRender fromString(String s) {
            return nameMap.get(s);
        }
    }

    @Override
    public void initialize() {
        List<ContentRender> renders = getScoped(ContentRender.class);
        RenderConvertor convertor = new RenderConvertor(renders);
        renderComboBox.setConverter(convertor);
        this.renders.addAll(renders);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        renderComboBox.setItems(renders);
    }

    public ContentRender getSelected(){
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
