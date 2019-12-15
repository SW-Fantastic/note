package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import org.controlsfx.control.PropertySheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 配置面板
 * 自定义用户自己的配置和喜好
 */
@FXMLView("/view/configView.fxml")
public class StartConfigView extends AbstractFxmlView{

    @Autowired
    protected UIConfig config;

    @PostConstruct
    protected void initUI() throws Exception{
        BorderPane pane = (BorderPane) this.getView();

        PropertySheet configSheet = new PropertySheet(UIUtil.getProperties(config));
        configSheet.getStyleClass().add("config-sheet");
        configSheet.setPropertyEditorFactory(item -> UIUtil.getEditor(item, config));
        configSheet.setModeSwitcherVisible(false);

        pane.setCenter(configSheet);
        UIUtil.configTheme(pane,config);

        /*ComboBox<String> combTheme = (ComboBox)getView().lookup("#cbxTheme");
        File[] themes = new File("configs/theme").listFiles();
        List<String> lstTheme = Arrays.asList(themes).stream().filter(item->item.isDirectory())
                .map(item->item.getName())
                .collect(Collectors.toList());
        combTheme.getItems().addAll(lstTheme);
        ComboBox<String> combImg = (ComboBox)getView().lookup("#cbxImg");
        File[] imgs = new File("configs/res").listFiles();
        List<String> lstImgs = Arrays.asList(imgs).stream().map(item->item.getName())
                .collect(Collectors.toList());
        combImg.getItems().addAll(lstImgs);
        combTheme.getSelectionModel().select(config.getTheme());
        combImg.getSelectionModel().select(config.getBackground());
        Slider slider = (Slider)getView().lookup("#editorFont");
        slider.setValue(config.getEditorFontSize());
        TextField txtFontSize = (TextField)getView().lookup("#txtEditSize");
        txtFontSize.textProperty().bind(slider.valueProperty().asString());
        UIUtil.configTheme((Pane)getView(),config);*/
    }

}
