package org.swdc.note.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.CollectionType;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.view.CollectSubView;

import java.net.URL;
import java.util.ResourceBundle;

public class CollectionSubViewController extends ViewController<CollectSubView> {


    @FXML
    private TreeView<CollectionType> collTypeTree;

    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {

        collTypeTree.setShowRoot(false);

    }

    @EventListener(type = RefreshEvent.class)
    public void refresh(RefreshEvent event) {

    }

}
