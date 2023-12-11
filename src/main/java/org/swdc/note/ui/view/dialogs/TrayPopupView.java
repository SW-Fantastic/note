package org.swdc.note.ui.view.dialogs;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.StageStyle;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.AbstractSwingDialogView;
import org.swdc.fx.view.AbstractSwingView;
import org.swdc.fx.view.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;


@View(viewLocation = "views/main/TrayPopupView.fxml",windowStyle = StageStyle.UNDECORATED)
public class TrayPopupView extends AbstractSwingDialogView {

    @Inject
    private MaterialIconsService iconsService = null;

    @PostConstruct
    public void initialize() {
        JDialog stage = getStage();
        stage.setSize(176,93);
        stage.setAlwaysOnTop(true);
        this.initViewToolButton("mainView","home");
        this.initViewToolButton("create","edit");
        this.initViewToolButton("open","attach_file");
        getView().setOnMouseExited(e -> {
            this.hide();
        });
    }

    private void initViewToolButton(String id,String icon) {
        Button btn = findById(id);
        if (btn == null) {
            return;
        }
        btn.setPadding(new Insets(4,4,4,4));
        btn.setFont(iconsService.getFont(FontSize.MIDDLE));
        btn.setText(iconsService.getFontIcon(icon));
    }

    public void show(MouseEvent e) {

        JDialog frame = getStage();
        GraphicsConfiguration configuration = frame.getGraphicsConfiguration();
        AffineTransform transform = configuration.getDefaultTransform();

        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = environment.getCenterPoint();

        centerPoint.setLocation(centerPoint.x / transform.getScaleX(), centerPoint.y / transform.getScaleY());
        if (e.getYOnScreen() < centerPoint.getY()) {
            frame.setLocation(
                    Double.valueOf(e.getXOnScreen() / transform.getScaleX()).intValue(),
                    0
            );
        } else {
            frame.setLocation(
                    Double.valueOf(e.getXOnScreen() / transform.getScaleX()).intValue() - frame.getWidth(),
                    Double.valueOf(e.getYOnScreen() / transform.getScaleY()).intValue() - frame.getHeight()
            );
        }
        this.show();
    }

}
