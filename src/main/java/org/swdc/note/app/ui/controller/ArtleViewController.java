package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.entity.ArtleContext;
import org.swdc.note.app.entity.ArtleType;
import org.swdc.note.app.event.ArtleListRefreshEvent;
import org.swdc.note.app.event.ArtleOpenEvent;
import org.swdc.note.app.event.ResetEvent;
import org.swdc.note.app.service.ArtleService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.ArtleCellView;
import org.swdc.note.app.ui.view.StartReadView;
import org.swdc.note.app.ui.view.classes.ClListView;
import org.swdc.note.app.util.UIUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 文档列表视图控制器
 */
@FXMLController
public class ArtleViewController implements Initializable {

    @Autowired
    private ArtleService artleService;

    @Autowired(required = false)
    private ClListView classicalView;

    @FXML
    private ListView<Artle> artleListView;

    @FXML
    private TableView<Artle> artleTable;

    @Autowired
    private UIConfig config;

    private ObservableList<Artle> listItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(UIUtil.isClassical()){
            artleTable.getColumns().forEach(col->{
                if(col.getId().equals("manage")){

                }else{
                    col.setCellValueFactory(new PropertyValueFactory<>(col.getId()));
                }
            });
            artleTable.setItems(listItems);
            artleTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                config.publishEvent(new ResetEvent(StartReadView.class));
            });
            artleTable.setOnMouseClicked(e->{

                Artle sel = artleTable.getSelectionModel().getSelectedItem();
                if(e.getClickCount() >= 2 && sel != null){
                    ArtleContext context = artleService.loadContext(sel);
                    classicalView.setContent(artleService.complie(context));
                    ArtleOpenEvent openEvent = new ArtleOpenEvent(sel);
                    config.publishEvent(openEvent);
                }
            });
        }else{
            artleListView.setCellFactory(listView->new ArtleCell(config.getComponent(ArtleCellView.class)));
            artleListView.setItems(listItems);
        }
    }

    /**
     * 选择的分类发生了改变 ，或者添加了新的文章，或者
     * 用户进行了搜索。
     * @param event 文档类型刷新事件
     */
    @EventListener
    public void onRefreshList(ArtleListRefreshEvent event){
        listItems.clear();
        if(event.isTypeRefresh()){
            ArtleType type = event.getModifyedType();
            listItems.addAll(artleService.loadArtles(type));
        }else if(event.isItemRefresh()){
            listItems.addAll(event.getRefList());
        }
    }

}
