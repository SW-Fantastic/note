package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.ArtleType;
import org.swdc.note.app.event.TypeRefreshEvent;
import org.swdc.note.app.service.TypeService;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class StartController implements Initializable {

    @FXML
    private TreeView<ArtleType> typeTreeView;

    @Autowired
    private TypeService typeService;

    private SimpleObjectProperty<TreeItem<ArtleType>> root = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeTreeView.setShowRoot(false);
        typeTreeView.rootProperty().bind(root);
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
