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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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

    private boolean isRestarting = false;

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
        isRestarting = true;
        Platform.runLater(() -> {
            getStage().close();
            if (config.getRunInBackground() && SystemTray.isSupported()){
                SystemTray.getSystemTray().remove(icon);
            }
            try {
                this.stop();
                this.init();
                this.start(new Stage());
            } catch (Exception e) {
                log.error(e);
            }
            isRestarting = false;
        });
    }

    /**
     * 为什么要这样做呢？我自己也没弄明白，springboot在关闭后并没有彻底
     * 关闭这个进程，而直接使用System.exit的话会导致spring卡住stop这里。
     */
    @PreDestroy
    public void onDestroy() {
        if (!isRestarting) {
            Thread shutdownThread = new Thread(() -> System.exit(0));
            shutdownThread.start();
        }
    }

}
