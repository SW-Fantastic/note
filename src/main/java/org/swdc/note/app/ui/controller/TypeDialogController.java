package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.swdc.note.app.entity.ArtleType;
import org.swdc.note.app.event.TypeRefreshEvent;
import org.swdc.note.app.service.TypeService;
import org.swdc.note.app.ui.view.TypeDialog;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * 分类对话框的控制器
 */
@FXMLController
public class TypeDialogController implements Initializable{

    @Autowired
    private TypeDialog typeDialog;

    @Autowired
    private TypeService typeService;

    @FXML
    private TreeView<ArtleType> treeView;

    @FXML
    private TextField txtName;

    private SimpleObjectProperty<TreeItem<ArtleType>> root = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @PostConstruct
    public void initTree(){
        root.set(typeService.getTypes());
        treeView.rootProperty().bind(root);
    }

    @FXML
    public void addNode(){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.initOwner(typeDialog.getStage());

        TreeItem<ArtleType> nodeParent = treeView.getSelectionModel().getSelectedItem();
        ArtleType type = new ArtleType();
        String name = txtName.getText();

        if(name == null || name.equals("")){
            alert.setContentText("请输入类别的名称");
            alert.showAndWait();
            return;
        }
        type.setName(name);
        if(nodeParent.getValue()!=null){
            type.setParentType(nodeParent.getValue());
        }
        if(!typeService.addType(type)){
            alert.setContentText("名称重复了，这是不可以的。");
            alert.showAndWait();
        }
    }

    @EventListener
    public void onTypeRefEvent(TypeRefreshEvent event){
        TreeItem<ArtleType> item = typeService.getTypes();
        item.setExpanded(true);
        root.set(item);
    }

}
