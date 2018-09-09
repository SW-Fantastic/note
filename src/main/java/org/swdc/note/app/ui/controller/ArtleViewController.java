package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.entity.ArtleType;
import org.swdc.note.app.event.ArtleListRefreshEvent;
import org.swdc.note.app.service.ArtleService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.ArtleCellView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 文档列表视图控制器
 */
@FXMLController
public class ArtleViewController implements Initializable {

    @Autowired
    private ArtleService artleService;

    @FXML
    private ListView<Artle> artleListView;

    @Autowired
    private UIConfig config;

    private ObservableList<Artle> listItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        artleListView.setCellFactory(listView->new ArtleCell(config.getComponent(ArtleCellView.class)));
        artleListView.setItems(listItems);
    }

    /**
     * 选择的分类发生了改变 ，或者添加了新的文章
     * @param event 文档类型刷新事件
     */
    @EventListener
    public void onRefreshList(ArtleListRefreshEvent event){
        listItems.clear();
        ArtleType type = event.getModifyedType();
        listItems.addAll(artleService.loadArtles(type));
    }

}
