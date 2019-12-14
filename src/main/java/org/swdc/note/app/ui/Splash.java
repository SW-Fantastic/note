package org.swdc.note.app.ui;

import de.felixroske.jfxsupport.SplashScreen;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;


public class Splash extends SplashScreen {

    @Override
    public String getImagePath() {
        return "/style/title.png";
    }

    @Override
    public Parent getParent() {
        ClassPathResource resource = new ClassPathResource(getImagePath());
        try {
            Image image = new Image(resource.getInputStream());
            Group gp = new Group();
            ImageView imageView = new ImageView(image);
            gp.getChildren().add(imageView);
            return gp;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
