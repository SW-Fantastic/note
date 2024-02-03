package org.swdc.note.ui.component.blocks;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class ImageBlock extends ArticleBlock {

    private VBox root;

    private ImageView imageView;

    private Image image;

    private byte[] data;

    private String name;

    public ImageBlock() {

    }

    @Override
    public Node getEditor() {
        if (root == null) {
            root = new VBox();
            root.getStyleClass().add("block-image");

            imageView = new ImageView();
            imageView.setSmooth(true);
            imageView.setPreserveRatio(true);

            Button add = new Button();
            add.setText("选择图片");
            add.setOnAction(e -> {
                FileChooser chooser = new FileChooser();
                chooser.setSelectedExtensionFilter(new FileChooser
                        .ExtensionFilter("图片","*.png", "*.jpg","*.gif","*.bmp","*.jpeg")
                );
                File imageFile = chooser.showOpenDialog(null);
                if (imageFile == null) {
                    return;
                }
                this.name = imageFile.getName();
                try (FileInputStream fin = new FileInputStream(imageFile)){
                    data = fin.readAllBytes();
                    this.image = new Image(new ByteArrayInputStream(data));
                    imageView.setImage(image);
                } catch (Exception ex) {
                }
            });

            root.widthProperty().addListener(e -> {
                imageView.setFitWidth(root.getWidth() - 60);
            });
            root.setMinHeight(120);
            root.setSpacing(8);
            root.setPadding(new Insets(4));
            root.setAlignment(Pos.CENTER);
            root.getChildren().addAll(imageView,add);

        }
        return root;
    }

    @Override
    public BlockData<Map<String,String>> getData() {
        Map<String,String> props = new HashMap<>();
        props.put("name", name);
        props.put("width", imageView.getFitWidth() + "");
        return new BlockData<>(
                this,
                props
        );
    }

    @Override
    protected String generate() {
        return Base64.getEncoder().encodeToString(data);
    }

    @Override
    public void setData(BlockData data) {
        if (root == null) {
            getEditor();
        }
        byte[] content = Base64.getDecoder()
                .decode(data.getSource());
        this.image = new Image(new ByteArrayInputStream(content));
        this.data = content;

        Map<String,String> header = (Map<String, String>) data.getContent();
        double width = Double.parseDouble(header.get("width"));

        this.name = header.get("name");
        this.imageView.setImage(image);
        this.imageView.setFitWidth(width);
    }
}
