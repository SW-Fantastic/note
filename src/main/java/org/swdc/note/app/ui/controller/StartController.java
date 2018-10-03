package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.entity.ArtleType;
import org.swdc.note.app.event.ArtleListRefreshEvent;
import org.swdc.note.app.event.TypeRefreshEvent;
import org.swdc.note.app.event.ViewChangeEvent;
import org.swdc.note.app.service.ArtleService;
import org.swdc.note.app.service.TypeService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.TypeTreeCell;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 */
@FXMLController
public class StartController implements Initializable {

    @FXML
    private TreeView<ArtleType> typeTreeView;

    @Autowired
    private TypeService typeService;

    @Autowired
    private ArtleService artleService;

    private SimpleObjectProperty<TreeItem<ArtleType>> root = new SimpleObjectProperty<>();

    @Autowired
    private UIConfig config;

    @FXML
    private TextField txtSeach;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeTreeView.setShowRoot(false);
        typeTreeView.rootProperty().bind(root);
        typeTreeView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue!=null && newValue.getValue()!=null){
                // 选择的节点发生改变，发布事件要求刷新列表，读取新类别的list
                config.publishEvent(new ArtleListRefreshEvent(newValue.getValue()));
                // 切换到列表
                config.publishEvent(new ViewChangeEvent("ListView"));
            }
        }));
        typeTreeView.setCellFactory(view->config.getComponent(TypeTreeCell.class));
    }

    @FXML
    public void onSearch(){
        if (txtSeach.getText() == null || txtSeach.getText().trim().equals("")){
            return;
        }
        // 搜索标题并且发布事件，刷新列表。
        List<Artle> list = artleService.searchArtleByTitle(txtSeach.getText());
        ArtleListRefreshEvent event = new ArtleListRefreshEvent(list);
        config.publishEvent(event);
        txtSeach.setText("");
    }

    @PostConstruct
    public void initTypeTree(){
        root.set(typeService.getTypes());
    }

    @EventListener
    public void onRefreshType(TypeRefreshEvent event){
        root.set(typeService.getTypes());
    }

}
