package org.swdc.note.ui.controllers.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Listener;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.view.dialogs.TypeCreateView;
import org.swdc.note.ui.view.dialogs.TypeSelectView;

import java.net.URL;
import java.util.ResourceBundle;

public class TypeSelectController extends FXController {

    @FXML
    private ListView<ArticleType> typeListView;

    @Aware
    private ArticleService service = null;

    private ObservableList<ArticleType> articleTypes = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeListView.setItems(articleTypes);
    }

    @Override
    public void initialize() {
        refresh(null);
    }

    @Listener(RefreshEvent.class)
    public void refresh(RefreshEvent event) {
        if (event != null && event.getData() != null){
            return;
        }
        articleTypes.clear();
        articleTypes.addAll(service.getTypes());
    }

    @FXML
    private void createType() {
        TypeCreateView createView = findView(TypeCreateView.class);
        createView.show();
    }

    @FXML
    private void cancel() {
        typeListView.getSelectionModel().clearSelection();
        TypeSelectView selectView = getView();
        selectView.close();
    }

    @FXML
    private void ok() {
        TypeSelectView selectView = getView();
        selectView.close();
    }

}
