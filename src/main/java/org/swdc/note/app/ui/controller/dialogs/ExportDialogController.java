package org.swdc.note.app.ui.controller.dialogs;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.file.FileFormatter;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.dialogs.ExportDialog;
import org.swdc.note.app.util.UIUtil;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 导出对话框的控制器。
 */
@FXMLController
public class ExportDialogController implements Initializable{

    @Autowired
    protected ExportDialog dialog;

    @Autowired
    private UIConfig config;

    @FXML
    protected TextField txtTargetName;

    @FXML
    protected TextField txtFileName;

    @FXML
    protected ComboBox<FileFormatter> combFormat;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        combFormat.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            txtFileName.setText("");
        });
    }

    @FXML
    protected void onOpen(){
        FileChooser fc = new FileChooser();
        fc.setTitle("导出文件");
        FileFormatter desc = combFormat.getSelectionModel().getSelectedItem();
        if(desc == null){
            UIUtil.showAlertDialog("请选择导出格式。", "提示", Alert.AlertType.ERROR, config);
            return;
        }
        fc.getExtensionFilters().addAll(desc.getFilters());
        File target = fc.showSaveDialog(dialog.getStage());
        if(target == null){
            return;
        }
        txtFileName.setText(target.getAbsolutePath());
    }

    @FXML
    protected void onExport(){
        FileFormatter desc = combFormat.getSelectionModel().getSelectedItem();

        if(desc == null){
            UIUtil.showAlertWithOwner("必须选择导出的格式才可以导出。", "提示", Alert.AlertType.ERROR,dialog.getStage());
            return;
        }
        if(txtFileName.getText()==null||txtFileName.getText().equals("")){
            UIUtil.showAlertWithOwner("请选择存储位置。", "提示", Alert.AlertType.ERROR,dialog.getStage());
            return;
        }
        File file = new File(txtFileName.getText());
        if(dialog.getTargetArticle()!=null){
            desc.processWrite(file,dialog.getTargetArticle());
        }else{
            desc.processWrite(file,dialog.getTargetGroup());
        }
        UIUtil.showAlertWithOwner("文档导出完毕。", "提示", Alert.AlertType.INFORMATION,dialog.getStage());
        dialog.getStage().close();
        this.txtFileName.setText("");
        this.txtTargetName.setText("");
    }

}
