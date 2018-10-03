package org.swdc.note.app.ui.view.dialogs;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.entity.ArtleType;
import org.swdc.note.app.event.ExportEvent;
import org.swdc.note.app.file.FileFormater;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 导出对话框的view界面。
 * 用户可以通过此界面导出markdown为pdf，html，pdf等格式。
 */
@FXMLView("/view/export.fxml")
public class ExportDialog extends AbstractFxmlView{

    @Autowired
    private UIConfig config;

    @Getter
    private Stage stage;

    @Getter
    private Artle targetArtle;

    @Getter
    private ArtleType targetGroup;

    @Autowired
    private List<FileFormater> fileFormaters;

    @PostConstruct
    protected void initUI() throws Exception {
        Pane pane = (Pane)getView();
        UIUtil.configTheme(pane,config);
        Platform.runLater(()->{
            stage = new Stage();
            stage.initOwner(GUIState.getStage());
            stage.setTitle("导出");
            stage.setScene(new Scene(pane));
            stage.setResizable(false);
        });
        Button btnOpen = (Button)getView().lookup("#open");
        btnOpen.setFont(UIConfig.getFontIconSmall());
        btnOpen.setText(String.valueOf(UIConfig.getAwesomeMap().get("folder_open")));
        ComboBox<FileFormater> combDescs = (ComboBox)getView().lookup("#formats");
        combDescs.getItems().addAll(fileFormaters.stream().filter(item->item.canWrite()).collect(Collectors.toList()));
    }

    public void initExport(ExportEvent exportEvent){
        this.targetArtle = null;
        this.targetGroup = null;
        TextField txtName =(TextField) getView().lookup("#targetName");
        if(exportEvent.isArtleExport()){
            this.targetArtle = exportEvent.getArtle();
            txtName.setText(targetArtle.getTitle());
        }else if(exportEvent.isTypeExport()){
            this.targetGroup = exportEvent.getArtleType();
            txtName.setText(targetGroup.getName());
        }
    }

}
