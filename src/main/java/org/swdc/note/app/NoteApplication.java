package org.swdc.note.app;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.scene.image.Image;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.swdc.note.app.ui.Splash;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.StartView;

import java.util.Collection;

@SpringBootApplication
public class NoteApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(NoteApplication.class,StartView.class,new Splash(),args);
    }

    @Override
    public Collection<Image> loadDefaultIcons() {
        try {
            return UIConfig.getImageIcons();
        }catch (Exception e){
            return super.loadDefaultIcons();
        }
    }
}
