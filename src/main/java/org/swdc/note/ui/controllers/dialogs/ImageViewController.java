package org.swdc.note.ui.controllers.dialogs;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.swdc.fx.view.ViewController;
import org.swdc.note.ui.view.dialogs.ImagesView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ImageViewController extends ViewController<ImagesView> {

    @FXML
    private ListView<String> listView;

    @FXML
    private ScrollPane scrPane;

    @Inject
    private Logger logger;

    private Map<String, ByteBuffer> images = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue!=null){
                ByteBuffer buffer = images.get(newValue);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.array());
                Image img = new Image(inputStream);
                scrPane.setContent(new ImageView(img));
            }
        }));
    }

    @FXML
    private void cancel() {
        this.listView.getSelectionModel().clearSelection();
        ImagesView view = getView();
        view.hide();
    }

    @FXML
    private void removeSelected() {
        String selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        images.remove(selected);
        listView.getItems().remove(selected);
    }

    @FXML
    private void openImage() {
        try {
            ImagesView view = getView();
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("图片","*.jpg","*.png","*.bmp","*.gif"));
            File file = chooser.showOpenDialog(view.getStage());
            if(file!=null&& file.exists()){
                if(images.keySet().contains(file.getName())){
                    return;
                }
                Path path = Paths.get(file.getAbsolutePath());
                byte[] data = Files.readAllBytes(path);
                ByteBuffer buffer = ByteBuffer.allocate(data.length);
                buffer.put(data);
                images.put(file.getName(), buffer);
                listView.getItems().add(file.getName());
            }
        } catch (Exception ex) {
            logger.error("error read image : " ,ex);
        }
    }

    public Map<String, ByteBuffer> getImages() {
        return images;
    }

    public void addImage(String name, ByteBuffer data) {
        if(images.keySet().contains(name)){
            return;
        }
        images.put(name, data);
        listView.getItems().add(name);
    }

    @FXML
    private void insertSelected() {
        ImagesView view = getView();
        view.hide();
    }

}
