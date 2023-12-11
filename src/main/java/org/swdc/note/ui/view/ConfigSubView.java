package org.swdc.note.ui.view;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.PropertySheet;
import org.slf4j.Logger;
import org.swdc.config.AbstractConfig;
import org.swdc.fx.FXResources;
import org.swdc.fx.config.ConfigViews;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.Toast;
import org.swdc.fx.view.View;
import org.swdc.note.config.AppConfig;
import org.swdc.note.config.RenderConfig;

@View(viewLocation = "views/main/ConfigSubView.fxml",stage = false)
public class ConfigSubView extends AbstractView {

    @Inject
    private AppConfig config;

    @Inject
    private FXResources resources;

    @Inject
    private RenderConfig renderConfig;

    @Inject
    private Logger logger;

    @PostConstruct
    public void initialize() {
        TabPane tabPane = findById("tabs");
        createToggleSettingView(tabPane,config,"通用");
        createToggleSettingView(tabPane,renderConfig,"渲染");
    }

    private void createToggleSettingView(TabPane tabPane, AbstractConfig config,String name) {
        ObservableList confGenerals = ConfigViews.parseConfigs(resources,config);
        PropertySheet generalConfSheet = new PropertySheet(confGenerals);
        generalConfSheet.setPropertyEditorFactory(ConfigViews.factory(resources));

        generalConfSheet.setModeSwitcherVisible(false);
        generalConfSheet.setSearchBoxVisible(false);
        generalConfSheet.getStyleClass().add("prop-sheet");

        Tab tab = new Tab(name);
        tab.setContent(generalConfSheet);
        tabPane.getTabs().add(tab);
    }



}
