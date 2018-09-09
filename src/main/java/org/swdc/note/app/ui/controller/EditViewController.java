package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.entity.ArtleContext;
import org.swdc.note.app.entity.ArtleType;
import org.swdc.note.app.service.ArtleService;
import org.swdc.note.app.ui.view.StartEditView;
import org.swdc.note.app.ui.view.dialogs.TypeDialog;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * 文档编辑视图控制器
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
        Artle artleCurr = artle;
        ArtleContext context;
        if(artle == null){
            artleCurr = new Artle();
            context = new ArtleContext();
        }else{
            context = artleService.loadContext(artleCurr);
        }
        artleCurr.setCreatedDate(new Date());
        artleCurr.setType(currType);
        artleCurr.setTitle(txtTitle.getText());
        context.setContent(editView.getDocument());
        context.setImageRes(editView.getImageRes());
        artleService.saveArtle(artleCurr,context);
    }

}
