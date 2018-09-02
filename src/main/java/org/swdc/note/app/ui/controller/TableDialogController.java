package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.ui.view.TableDialog;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by lenovo on 2018/9/1.
 */
@FXMLController
public class TableDialogController implements Initializable {

    @Autowired
    private TableDialog dlg;

    @FXML
    private TextField txtCols;

    @FXML
    private TextField txtRows;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void addTable(){
        if(txtCols.getText()!=null&&!txtCols.getText().equals("")){
            dlg.setColNum(Integer.valueOf(txtCols.getText()));
        }
        if(txtRows.getText()!=null&&!txtRows.getText().equals("")){
            dlg.setRowNum(Integer.valueOf(txtRows.getText()));
        }
        if(dlg.getStage().isShowing()){
            dlg.getStage().close();
        }
    }

}
