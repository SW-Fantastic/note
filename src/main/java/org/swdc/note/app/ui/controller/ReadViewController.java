package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.entity.ArtleContext;
import org.swdc.note.app.entity.ArtleType;
import org.swdc.note.app.event.ArtleOpenEvent;
import org.swdc.note.app.event.ExportEvent;
import org.swdc.note.app.event.ResetEvent;
import org.swdc.note.app.event.TypeImportEvent;
import org.swdc.note.app.file.FileFormater;
import org.swdc.note.app.service.ArtleService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.StartReadView;
import org.swdc.note.app.ui.view.dialogs.TypeDialog;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 阅读视图的控制器。
 */
@FXMLController
public class ReadViewController implements Initializable {

    /**
     * 当前读取的实体
     */
    private Artle artle;

    @Autowired
    private StartReadView readView;

    @Autowired
    private UIConfig config;

    @Autowired
    private ArtleService artleService;

    @Autowired
    private List<FileFormater> formaters;

    @Autowired
    private TypeDialog typeDialog;

    @FXML
    protected TextField txtTitle;

    @FXML
    protected Label lblDate;

    @FXML
    protected Button btnImport;

    @FXML
    protected Button btnExport;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @EventListener
    public void onArtleOpen(ArtleOpenEvent e){
        Artle artle = e.getArtle();
        this.artle = artle;
        ArtleContext context = artleService.loadContext(artle);
        readView.getWebView().getEngine().loadContent(artleService.complie(context));
        txtTitle.setText(artle.getTitle());
        lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(artle.getCreatedDate()));
        btnExport.setVisible(true);
        btnImport.setVisible(false);
    }

    @FXML
    protected void onOpen(){
        List<FileChooser.ExtensionFilter> list = new ArrayList<>();
        formaters.stream().filter(item->item.canRead()).map(item->item.getFilters()).forEach(list::addAll);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开");
        fileChooser.getExtensionFilters().addAll(list);
        File file = fileChooser.showOpenDialog(GUIState.getStage());
        FileChooser.ExtensionFilter filter = fileChooser.getSelectedExtensionFilter();
        formaters.stream().filter(item->item.getFilters().contains(filter)).findFirst().ifPresent(target->{
            Artle artle = target.processRead(file,Artle.class);
            if(artle != null){
                // 单个数据文件打开
                ArtleContext context = artle.getContext();
                readView.getWebView().getEngine().loadContent(artleService.complie(context));
                txtTitle.setText(artle.getTitle());
                lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(artle.getCreatedDate()));
                this.artle = artle;
                btnExport.setVisible(false);
                btnImport.setVisible(true);
            }else {
                // 数据合并导入
                TypeImportEvent importEvent = new TypeImportEvent(file,target);
                config.publishEvent(importEvent);
            }
        });
    }

    @FXML
    protected void onImport(){
        if(artle!=null){
            typeDialog.getStage().showAndWait();
            ArtleType type = typeDialog.getArtleType();
            if(type != null){
                artle.setType(type);
                artleService.saveArtle(artle,artle.getContext());
                btnImport.setVisible(false);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setTitle("提示");
                alert.setContentText("文档已经成功导入系统。");
                alert.initOwner(GUIState.getStage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    protected void onExport(){
        if(this.artle!=null){
            ExportEvent exportEvent = new ExportEvent(artle);
            config.publishEvent(exportEvent);
        }
    }

    @EventListener
    protected void onReset(ResetEvent event){
        if(event.getSource().equals(StartReadView.class) || event.getSource() == null){
            if(readView.getStage()!=null && readView.getStage().isShowing()){
                return;
            }
            this.artle = null;
            readView.getWebView().getEngine().loadContent("");
            lblDate.setText("");
            txtTitle.setText("");
        }
    }

}
