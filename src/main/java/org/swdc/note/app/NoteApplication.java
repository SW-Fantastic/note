package org.swdc.note.app;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.apachecommons.CommonsLog;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.event.ReLaunchEvent;
import org.swdc.note.app.ui.Splash;
import org.swdc.note.app.ui.desktop.TrayMenuIcon;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.StartView;
import org.swdc.note.app.ui.view.classes.ClStartView;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.io.File;
import java.util.Collection;

@CommonsLog
@SpringBootApplication
public class NoteApplication extends AbstractJavaFxApplicationSupport {

    @Autowired
    private TrayMenuIcon icon;

    @Autowired
    private UIConfig config;

    public static void main(String[] args) throws Exception{
        BeautyEyeLNFHelper.launchBeautyEyeLNF();
        launch(NoteApplication.class,StartView.class,new Splash(),args);
    }

    @Override
    public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            try {
                UIConfig config = ctx.getBean(UIConfig.class);
                stage.getScene().getStylesheets().add(new File("./configs/theme/" + config.getTheme() + "/stage.css").toURI().toURL().toExternalForm());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stage.close();
            ctx.stop();
            System.exit(0);
        }));
    }

    @PostConstruct
    protected void initUI(){
        if(config.getRunInBackground()){
            Platform.setImplicitExit(false);
            if (SystemTray.isSupported()) {
                SystemTray tray = SystemTray.getSystemTray();
                try {
                    tray.add(icon);
                } catch (AWTException e) {
                    log.error("create tray icon cause a error: " + e.getCause());
                }
            }
        }
    }

    @Override
    public Collection<Image> loadDefaultIcons() {
        try {
            return UIConfig.getImageIcons();
        }catch (Exception e){
            return super.loadDefaultIcons();
        }
    }

    @EventListener(ReLaunchEvent.class)
    public void relaunch(){
        Platform.runLater(() -> {
            getStage().close();
            if (SystemTray.isSupported()){
                SystemTray.getSystemTray().remove(icon);
            }
            try {
                this.stop();
                this.init();
                this.start(new Stage());
            } catch (Exception e) {
                log.error(e);
            }
        });
    }

}
