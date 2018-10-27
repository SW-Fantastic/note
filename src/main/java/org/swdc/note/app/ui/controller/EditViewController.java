package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.entity.ArtleContext;
import org.swdc.note.app.entity.ArtleType;
import org.swdc.note.app.event.ArtleEditEvent;
import org.swdc.note.app.event.ResetEvent;
import org.swdc.note.app.service.ArtleService;
import org.swdc.note.app.ui.view.StartEditView;
import org.swdc.note.app.ui.view.dialogs.TypeDialog;
import org.swdc.note.app.util.DataUtil;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * 文档编辑视图控制器.
 */
@FXMLController
public class EditViewController implements Initializable{

    private ArtleType currType;
    private Artle artle;

    @FXML
    private TextField txtType;

    @FXML
    private TextField txtTitle;

    @Autowired
    private TypeDialog typeDialog;

    @Autowired
    private ArtleService artleService;

    @Autowired
    private StartEditView editView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void selType() {
        Stage stg = typeDialog.getStage();
        if(editView.getStage() != null && stg.getOwner() != editView.getStage()){
            stg.initOwner(editView.getStage());
        }
        if(stg.isShowing()){
            stg.requestFocus();
        }else{
            stg.showAndWait();
        }
        this.currType = typeDialog.getArtleType();
        if(currType != null){
            txtType.setText(currType.getName());
        }
    }

    @FXML
    public void saveArtle(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("提示");
        alert.initOwner(GUIState.getStage());
        if(currType == null){
            alert.setContentText("请先选择分类");
            alert.showAndWait();
            return;
        }
        if(txtTitle.getText() == null || txtTitle.getText().equals("")){
            alert.setContentText("请输入标题");
            alert.showAndWait();
            return;
        }
        // 封装数据
        Artle artleCurr = new Artle();
        ArtleContext context = new ArtleContext();
        if(artle != null){
            // 有artle对象，应该是在修改，先复制以前的数据
            ArtleContext contextOld = artleService.loadContext(artle);
            DataUtil.updateProperties(artle,artleCurr);
            DataUtil.updateProperties(contextOld,context);
        }
        // 写入新数据
        artleCurr.setCreatedDate(new Date());
        artleCurr.setType(currType);
        artleCurr.setTitle(txtTitle.getText());
        context.setContent(editView.getDocument());
        context.setImageRes(editView.getImageRes());
        artleService.saveArtle(artleCurr,context);
    }

    /**
     * 处理文档的编辑请求事件。
     * @param event 编辑事件
     */
    @EventListener
    public void onArtleEdit(ArtleEditEvent event){
        Artle artle = event.getSource();
        this.artle = artle;
        this.currType = artle.getType();
        this.txtType.setText(currType.getName());
        this.txtTitle.setText(artle.getTitle());
        ArtleContext context = artleService.loadContext(artle);
        editView.setContext(context.getContent());
    }

    @EventListener
    public void onReset(ResetEvent resetEvent){
        if(resetEvent.getSource().equals(StartEditView.class)||resetEvent.getSource() == null){
            this.editView.reset();
            this.txtType.setText(null);
            this.currType = null;
            this.artle = null;
            this.txtTitle.setText(null);
        }
    }

}
