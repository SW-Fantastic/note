package org.swdc.note.ui.view.dialogs;


import jakarta.annotation.PostConstruct;
import javafx.scene.control.TextField;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.entities.CollectionType;
import org.swdc.note.ui.controllers.dialogs.TypeCollectionEditController;

@View(dialog = true,title = "分类编辑",viewLocation = "views/dialogs/TypeCollectTypeView.fxml")
public class TypeCollectionEditView extends AbstractView {

    private CollectionType parent;

    @PostConstruct
    public void initialize() {
        this.getStage().setOnCloseRequest(e -> {
            TextField text = findById("txtCollectName");
            text.setText("");
        });
    }

    public void setParent(CollectionType parent) {
        this.parent = parent;
    }

    public CollectionType getParent() {
        return parent;
    }

    public void setType(CollectionType type) {
        TypeCollectionEditController controller = getController();
        controller.setType(type);
    }
}
