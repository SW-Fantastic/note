package org.swdc.note.ui.view;

import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.MaterialIconsService;

import java.util.HashMap;

@View(title = "幻想笔记", resizeable = true,background = true)
public class MainView extends FXView {

    @Aware
    private MaterialIconsService iconsService = null;

    private ToggleGroup group;

    private HashMap<Class, FXView> subViews = new HashMap<>();

    private SimpleDoubleProperty subViewWidth = new SimpleDoubleProperty();
    private SimpleDoubleProperty subViewHeight = new SimpleDoubleProperty();


    @Override
    public void initialize() {
        super.initialize();
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
        group.selectToggle(findById("type"));
    }

    private void viewToggleChange(Observable observable, Toggle oldVal, Toggle newVal) {
        ToggleButton tgOld = (ToggleButton)oldVal;
        ToggleButton tgNew = (ToggleButton)newVal;
        if (tgNew == null) {
            group.selectToggle(tgOld);
            this.viewChange(tgOld.getId());
            return;
        }
        this.viewChange(tgNew.getId());
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

    private void viewChange(Class viewContent) {
        BorderPane borderPane = findById("content");
        FXView view = subViews.get(viewContent);
        if (view == null) {
            view = findView(viewContent);
            BorderPane contentRoot =  view.getView();
            contentRoot.prefWidthProperty().bind(subViewWidth);
            contentRoot.prefHeightProperty().bind(subViewHeight);
            subViews.put(viewContent,view);
        }
        borderPane.setCenter(view.getView());
        borderPane.requestLayout();
    }

    private void viewChange(String name) {
        switch (name) {
            case "type": viewChange(TypeSubView.class);break;
            case "conf": viewChange(ConfigSubView.class);break;
        }
    }

}
