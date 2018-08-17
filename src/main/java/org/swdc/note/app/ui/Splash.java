package org.swdc.note.app.ui;

import de.felixroske.jfxsupport.SplashScreen;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;


public class Splash extends SplashScreen {

    @Override
    public String getImagePath() {
        return "/style/title.png";
    }

    @Override
    public Parent getParent() {
        Group gp = new Group();
        ImageView imageView = new ImageView(this.getClass().getResource(this.getImagePath()).toExternalForm());
        gp.getChildren().add(imageView);
        return gp;
    }
}
