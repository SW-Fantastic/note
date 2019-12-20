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
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.event.ExportEvent;
import org.swdc.note.app.file.Formatter;
import org.swdc.note.app.service.FormatterService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;
import java.util.List;

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
    private Article targetArticle;

    @Getter
    private ArticleType targetGroup;

    @Autowired
    private FormatterService formatterService;

    @PostConstruct
    protected void initUI() throws Exception {
        Pane pane = (Pane)getView();
        UIUtil.configTheme(pane,config);
        Platform.runLater(()->{
            stage = new Stage();
            stage.getIcons().addAll(UIConfig.getImageIcons());
            stage.initOwner(GUIState.getStage());
            stage.setTitle("导出");
            stage.setScene(new Scene(pane));
            stage.setResizable(false);
        });
        Button btnOpen = (Button)getView().lookup("#open");
        btnOpen.setFont(UIConfig.getFontIconSmall());
        btnOpen.setText(String.valueOf(UIConfig.getAwesomeMap().get("folder_open")));
        ComboBox<Formatter> combDescs = (ComboBox)getView().lookup("#formats");
        List<Formatter> formatters = formatterService.getAllFormatters();
        combDescs.getItems().addAll(formatters);
    }

    public void initExport(ExportEvent exportEvent){
        this.targetArticle = null;
        this.targetGroup = null;
        TextField txtName =(TextField) getView().lookup("#targetName");
        if(exportEvent.isArtleExport()){
            setBatch(false);
            this.targetArticle = exportEvent.getArticle();
            txtName.setText(targetArticle.getTitle());
        }else if(exportEvent.isTypeExport()){
            setBatch(true);
            this.targetGroup = exportEvent.getArticleType();
            txtName.setText(targetGroup.getName());
        }
    }

    public void setBatch(boolean batch) {
        Platform.runLater(() -> {
            ComboBox<Formatter> combDescs = (ComboBox)getView().lookup("#formats");
            List<Formatter> formatters = batch ? formatterService.getBatchFormatters() : formatterService.getDocumentFormatters();
            combDescs.getItems().clear();
            combDescs.getItems().addAll(formatters);
        });
    }
}
