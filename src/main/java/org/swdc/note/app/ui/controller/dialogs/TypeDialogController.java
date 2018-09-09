package org.swdc.note.app.ui.controller.dialogs;

import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.ArtleType;
import org.swdc.note.app.event.TypeRefreshEvent;
import org.swdc.note.app.service.TypeService;
import org.swdc.note.app.ui.view.dialogs.TypeDialog;

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
        TreeItem<ArtleType> treeItem = typeService.getTypes();
        treeItem.setExpanded(true);
        root.set(treeItem);
        treeView.setShowRoot(false);
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
        if(nodeParent!=null && nodeParent.getValue()!=null){
            type.setParentType(nodeParent.getValue());
        }
        if(!typeService.addType(type)){
            alert.setContentText("名称重复了，这是不可以的。");
            alert.showAndWait();
        }
    }

    @FXML
    public void delNode(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.initOwner(typeDialog.getStage());
        TreeItem<ArtleType> nodeSel = treeView.getSelectionModel().getSelectedItem();
        if(nodeSel == null){
            return;
        }
        if(!typeService.delType(nodeSel.getValue(),false)){
            alert.setContentText("此分类下含有其他内容。");
            alert.showAndWait();
        }
    }

    @FXML
    public void selectType(){
        TreeItem<ArtleType> nodeParent = treeView.getSelectionModel().getSelectedItem();
        Optional.ofNullable(nodeParent).ifPresent(type -> {
            if(type.getValue() == null){
                return;
            }
            typeDialog.setArtleType(type.getValue());
            typeDialog.getStage().close();
        });
    }

    @FXML
    public void cleanFocus(){
        treeView.getSelectionModel().clearSelection();
    }

    @EventListener
    public void onTypeRefEvent(TypeRefreshEvent event){
        TreeItem<ArtleType> item = typeService.getTypes();
        item.setExpanded(true);
        root.set(item);
    }

}
