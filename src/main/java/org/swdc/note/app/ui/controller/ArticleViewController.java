package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleContext;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.event.ArticleListRefreshEvent;
import org.swdc.note.app.event.ArticleOpenEvent;
import org.swdc.note.app.event.ResetEvent;
import org.swdc.note.app.service.ArticleService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.ArticleCellView;
import org.swdc.note.app.ui.view.ManageCellView;
import org.swdc.note.app.ui.view.StartReadView;
import org.swdc.note.app.ui.view.classes.ClListView;
import org.swdc.note.app.util.UIUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 文档列表视图控制器
 */
@FXMLController
public class ArticleViewController implements Initializable {

    @Autowired
    private ArticleService articleService;

    @Autowired(required = false)
    private ClListView classicalView;

    @FXML
    private ListView<Article> articleListView;

    @FXML
    private TableView<Article> articleTable;

    @Autowired
    private UIConfig config;

    private ObservableList<Article> listItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(UIUtil.isClassical()){
            articleTable.getColumns().forEach(col->{
                if(col.getId().equals("manage")){
                    TableColumn<Article,Long> column = (TableColumn<Article, Long>) col;
                    column.setCellFactory(colElem->new ManageCell(config.getComponent(ManageCellView.class)));
                }else{
                    col.setCellValueFactory(new PropertyValueFactory<>(col.getId()));
                }
            });
            articleTable.setItems(listItems);
            articleTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                config.publishEvent(new ResetEvent(StartReadView.class));
            });
            articleTable.setOnMouseClicked(e->{

                Article sel = articleTable.getSelectionModel().getSelectedItem();
                if(e.getClickCount() >= 2 && sel != null){
                    ArticleContext context = articleService.loadContext(sel);
                    classicalView.setContent(articleService.renderHTML(context));
                    ArticleOpenEvent openEvent = new ArticleOpenEvent(sel);
                    config.publishEvent(openEvent);
                }
            });
        }else{
            articleListView.setCellFactory(listView->new ArticleCell(config.getComponent(ArticleCellView.class)));
            articleListView.setItems(listItems);
        }
    }

    /**
     * 选择的分类发生了改变 ，或者添加了新的文章，或者
     * 用户进行了搜索。
     * @param event 文档类型刷新事件
     */
    @EventListener
    public void onRefreshList(ArticleListRefreshEvent event){
        listItems.clear();
        if(event.isTypeRefresh()){
            ArticleType type = event.getModifyedType();
            listItems.addAll(articleService.loadArticles(type));
        }else if(event.isItemRefresh()){
            listItems.addAll(event.getRefList());
        }
    }

}
