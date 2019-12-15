package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.event.ArticleListRefreshEvent;
import org.swdc.note.app.service.ArticleService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.ArticleCellView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 文档列表视图控制器
 */
@FXMLController
public class ArticleViewController implements Initializable {

    @Autowired
    private ArticleService articleService;

    @FXML
    private ListView<Article> articleListView;

    @Autowired
    private UIConfig config;

    private ObservableList<Article> listItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        articleListView.setCellFactory(listView->new ArticleCell(config.getComponent(ArticleCellView.class)));
        articleListView.setItems(listItems);
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
