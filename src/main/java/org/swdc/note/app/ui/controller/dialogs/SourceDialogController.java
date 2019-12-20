package org.swdc.note.app.ui.controller.dialogs;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.event.ArticleEditEvent;
import org.swdc.note.app.file.Formatter;
import org.swdc.note.app.service.FormatterService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.dialogs.SourceDialog;
import org.swdc.note.app.util.DataUtil;
import org.swdc.note.app.util.UIUtil;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@FXMLController
public class SourceDialogController implements Initializable {

    @FXML
    private TextField txtLocation;

    @Autowired
    private FormatterService formatterService;

    @Autowired
    private UIConfig config;

    @Autowired
    private SourceDialog dialog;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void onOpenFile() {
        FileChooser fileChooser = new FileChooser();
        List<FileChooser.ExtensionFilter> list = new ArrayList<>();
        List<Formatter> formatters = formatterService.getDocumentFormatters();
        for (Formatter formatter : formatters) {
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(formatter.getFormatName(), "*."+formatter.getFormatExtension());
            list.add(filter);
        }
        fileChooser.getExtensionFilters().addAll(list);
        File file = fileChooser.showOpenDialog(GUIState.getStage());
        if (file == null) {
            return;
        }
        txtLocation.setText(file.getAbsolutePath());
    }

    @FXML
    public void onCreateDocument() {
        try {
            if (txtLocation.getText().trim().equals("")) {
                UIUtil.showAlertDialog("请首先选择文件或输入URL。","提示", Alert.AlertType.WARNING,config);
                return;
            }
            String path = txtLocation.getText();
            if (path.startsWith("http")) {
                File tempFile = DataUtil.downloadHttpContent(path);
                if (tempFile == null) {
                    UIUtil.showAlertDialog("载入失败。","提示", Alert.AlertType.ERROR,config);
                    return;
                }
                resolveDocumentFile(tempFile);
                tempFile.delete();
            } else {
                File file = new File(path);
                if (!file.exists()) {
                    UIUtil.showAlertDialog("文件不存在，或者此类型的url暂不支持。","提示", Alert.AlertType.WARNING,config);
                    return;
                }
                resolveDocumentFile(file);
            }
            txtLocation.setText("");
            dialog.hide();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void resolveDocumentFile (File file) {
        String[] name = file.getName().split("[.]");
        String ext = name[name.length - 1];
        Formatter formatter = formatterService.getDocumentFormatterByExtension(ext,false);
        if (formatter == null) {
            return;
        }
        Article article = (Article) formatter.readDocument(file);
        if (article == null) {
            return;
        }
        ArticleEditEvent editEvent = new ArticleEditEvent(article);
        editEvent.setContextFilled(true);
        config.publishEvent(editEvent);
    }

}
