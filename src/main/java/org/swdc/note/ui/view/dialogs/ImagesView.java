package org.swdc.note.ui.view.dialogs;

import javafx.scene.control.ListView;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Scope;
import org.swdc.fx.anno.ScopeType;
import org.swdc.fx.anno.View;
import org.swdc.note.ui.controllers.dialogs.ImageViewController;

import java.nio.ByteBuffer;
import java.util.Map;

@View(dialog = true, title = "图片")
@Scope(ScopeType.MULTI)
public class ImagesView extends FXView {

    @Override
    public void initialize() {
        this.getStage().setOnCloseRequest(e -> {
            this.clear();
        });
    }

    public String getSelectedImageName() {
        ListView<String> listView = findById("imageList");
        String image = listView.getSelectionModel().getSelectedItem();
        listView.getSelectionModel().clearSelection();
        return image;
    }

    public Map<String, ByteBuffer> getImages() {
        ImageViewController controller = getLoader().getController();
        return controller.getImages();
    }

    public void addImage(String name, byte[] data) {
        ImageViewController controller = getLoader().getController();
        controller.addImage(name, ByteBuffer.wrap(data));
    }

    public void clear() {
        ListView<String> listView = findById("imageList");
        listView.getSelectionModel().clearSelection();
    }

}
