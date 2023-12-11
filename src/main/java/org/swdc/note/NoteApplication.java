package org.swdc.note;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.swdc.data.EMFProviderFactory;
import org.swdc.dependency.DependencyContext;
import org.swdc.dependency.EnvironmentLoader;
import org.swdc.fx.FXApplication;
import org.swdc.fx.SWFXApplication;
import org.swdc.note.config.AppConfig;
import org.swdc.note.core.EntityManagerProviderImpl;
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

@SWFXApplication(splash = SplashScreen.class,icons = {
        "icon16.png","icon24.png","icon32.png","icon48.png","icon64.png","icon72.png"
},configs = AppConfig.class,assetsFolder = "./assets")
public class NoteApplication extends FXApplication {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void onConfig(EnvironmentLoader loader) {
        loader.withProvider(EntityManagerProviderImpl.class);
    }

    @Override
    public void onStarted(DependencyContext dependencyContext) {

        EMFProviderFactory factory = dependencyContext.getByClass(EMFProviderFactory.class);
        factory.create();

        AppConfig config = dependencyContext.getByClass(AppConfig.class);
        if (config.getShowMainView()) {
            dependencyContext.getByClass(MainView.class).show();
        }
    }



}
