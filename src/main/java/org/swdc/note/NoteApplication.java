package org.swdc.note;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.swdc.fx.FXApplication;
import org.swdc.fx.FXSplash;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.SFXApplication;
import org.swdc.fx.container.ApplicationContainer;
import org.swdc.fx.properties.ConfigManager;
import org.swdc.fx.resource.source.ModulePathResource;
import org.swdc.fx.services.ServiceManager;
import org.swdc.note.config.AppConfig;
import org.swdc.note.core.proto.URLResolverManager;
import org.swdc.note.core.render.RendersManager;
import org.swdc.note.ui.controllers.GlobalKeyListener;
import org.swdc.note.ui.view.MainView;
import org.swdc.note.ui.view.dialogs.TrayPopupView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@SFXApplication(splash = FXSplash.class, mainView = MainView.class,singleton = true)
public class NoteApplication extends FXApplication {

    private List<Image> icons;

    private TrayIcon icon;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void onLaunch(ConfigManager configManager) {
        configManager.register(AppConfig.class);
    }

    @Override
    protected void onStart(ApplicationContainer container) {
        container.register(RendersManager.class);
        container.register(URLResolverManager.class);
        container.getComponent(ServiceManager.class).register(GlobalKeyListener.class);
        if (SystemTray.isSupported()) {
            try {
                SystemTray tray = SystemTray.getSystemTray();
                icon = new TrayIcon(ImageIO.read(new ModulePathResource(this.getClass().getModule(),"appIcons/icon16.png").getInputStream()));
                icon.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() >= 2) {
                            if (e.getButton() == MouseEvent.BUTTON1) {
                                Platform.runLater(()-> {
                                    MainView mainView = findComponent(MainView.class);
                                    mainView.show();
                                });
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3){
                            Platform.runLater(() -> {
                                TrayPopupView view = findComponent(TrayPopupView.class);
                                int y = e.getY();
                                Toolkit toolkit = Toolkit.getDefaultToolkit();
                                double height = toolkit.getScreenSize().getHeight();
                                JWindow stg = view.getSWingStage();
                                if(y > height / 2) {
                                    stg.setLocation(e.getX() - stg.getWidth() / 2, e.getY() - stg.getHeight() - 6);
                                    view.show();
                                } else {
                                    stg.setLocation(e.getX() - stg.getWidth() / 2, e.getY() + stg.getHeight());
                                    view.show();
                                }
                            });
                        }
                    }
                });
                tray.add(icon);
                Platform.setImplicitExit(false);
            } catch (Exception e) {
                logger.error("failed to create tray icon.", e);
            }
        }
    }

    @Override
    protected void appHasStarted(FXView mainView) {
        AppConfig config = findComponent(AppConfig.class);
        if (config.getShowMainView()) {
            mainView.show();
        }
    }

    @Override
    protected void onStop(ApplicationContainer container) {
        if (icon == null) {
            return;
        }
        SystemTray.getSystemTray().remove(icon);
    }

    @Override
    protected List<Image> loadIcons() {
        if (icons != null) {
            return icons;
        }
        this.icons = Arrays.asList(
                new Image(getResource("appIcons/icon16.png")),
                new Image(getResource("appIcons/icon24.png")),
                new Image(getResource("appIcons/icon32.png")),
                new Image(getResource("appIcons/icon48.png")),
                new Image(getResource("appIcons/icon64.png")),
                new Image(getResource("appIcons/icon72.png"))
        );
        return icons;
    }

    private InputStream getResource(String path) {
        Module module = this.getClass().getModule();
        return new ModulePathResource(module, path).getInputStream();
    }

}
