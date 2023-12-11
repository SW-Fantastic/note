package org.swdc.note.ui.view;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;

import java.util.HashMap;

@View(title = "幻想笔记",viewLocation = "views/main/MainView.fxml",background = "background.jpg")
public class MainView extends AbstractView {

    @Inject
    private MaterialIconsService iconsService = null;

    @Inject
    private TypeSubView typeSubView;

    @Inject
    private ConfigSubView configSubView;


    private ToggleGroup group;

    private SimpleDoubleProperty subViewWidth = new SimpleDoubleProperty();

    private SimpleDoubleProperty subViewHeight = new SimpleDoubleProperty();


    @PostConstruct
    public void initialize() {
        this.group = new ToggleGroup();
        this.initViewToolButton("type", "list");
        this.initViewToolButton("conf","settings");

        group.selectedToggleProperty().addListener(this::viewToggleChange);

        Stage stage = this.getStage();
        stage.setMinWidth(920);
        stage.setMinHeight(600);

        BorderPane root = findById("rootContainer");
        subViewHeight.bind(root.heightProperty());
        subViewWidth.bind(root.widthProperty().subtract(62));

        initialSizes(typeSubView);
        initialSizes(configSubView);

        group.selectToggle(findById("type"));

    }

    private void initialSizes(AbstractView view) {
        BorderPane root = (BorderPane) view.getView();
        root.setPrefSize(subViewWidth.getValue(),subViewHeight.getValue());
        root.prefWidthProperty().bind(subViewWidth);
        root.prefHeightProperty().bind(subViewHeight);
    }

    private void viewToggleChange(Observable observable, Toggle oldVal, Toggle newVal) {
        ToggleButton tgOld = (ToggleButton)oldVal;
        ToggleButton tgNew = (ToggleButton)newVal;
        if (tgNew == null) {
            group.selectToggle(tgOld);
            this.viewChange(tgOld.getId(),0);
            return;
        }
        this.viewChange(tgNew.getId(),0);
    }


    private void initViewToolButton(String id,String icon) {
        ToggleButton toggleButton = findById(id);
        if (toggleButton == null) {
            return;
        }
        toggleButton.setPadding(new Insets(4,4,4,4));
        toggleButton.setFont(iconsService.getFont(FontSize.MIDDLE_SMALL));
        toggleButton.setText(iconsService.getFontIcon(icon));
        group.getToggles().add(toggleButton);
    }



    private void viewChange(String name, int count) {
        BorderPane borderPane = findById("content");
        borderPane.setCenter(null);
        switch (name) {
            case "type" : {
                borderPane.setCenter(typeSubView.getView());
                break;
            }
            case "conf" : {
                borderPane.setCenter(configSubView.getView());
                break;
            }
        }
        if (count < 1) {
            viewChange(name,++count);
        }
    }

}
