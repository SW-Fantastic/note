package org.swdc.note.app;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.swdc.note.app.ui.Splash;
import org.swdc.note.app.ui.view.StartView;

@SpringBootApplication
public class NoteApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(NoteApplication.class,StartView.class,new Splash(),args);
    }
}
