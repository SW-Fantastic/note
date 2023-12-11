package org.swdc.note.ui.view.dialogs;

import jakarta.annotation.PostConstruct;
import javafx.scene.control.ListView;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.ui.controllers.dialogs.ImageViewController;

import java.nio.ByteBuffer;
import java.util.Map;

@View(dialog = true, title = "图片",multiple = true,viewLocation = "views/main/ImagesView.fxml")
public class ImagesView extends AbstractView {

    @PostConstruct
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
        ImageViewController controller = getController();
        return controller.getImages();
    }

    public void addImage(String name, byte[] data) {
        ImageViewController controller = getController();
        controller.addImage(name, ByteBuffer.wrap(data));
    }

    public void clear() {
        ListView<String> listView = findById("imageList");
        listView.getSelectionModel().clearSelection();
    }

}
