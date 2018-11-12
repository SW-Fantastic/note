package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.event.ArticleListRefreshEvent;
import org.swdc.note.app.event.TypeRefreshEvent;
import org.swdc.note.app.event.ViewChangeEvent;
import org.swdc.note.app.service.ArticleService;
import org.swdc.note.app.service.TypeService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.TypeTreeCell;
import org.swdc.note.app.util.UIUtil;

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
    private TreeView<ArticleType> typeTreeView;

    @Autowired
    private TypeService typeService;

    @Autowired
    private ArticleService articleService;

    private SimpleObjectProperty<TreeItem<ArticleType>> root = new SimpleObjectProperty<>();

    @Autowired
    private UIConfig config;

    @FXML
    private TextField txtSearch;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeTreeView.setShowRoot(false);
        typeTreeView.rootProperty().bind(root);
        typeTreeView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue!=null && newValue.getValue()!=null){
                // 选择的节点发生改变，发布事件要求刷新列表，读取新类别的list
                config.publishEvent(new ArticleListRefreshEvent(newValue.getValue()));
                // 切换到列表
                config.publishEvent(new ViewChangeEvent("ListView"));
            }
        }));
        typeTreeView.setCellFactory(view->config.getComponent(TypeTreeCell.class));
    }

    @FXML
    public void onSearch(){
        if (txtSearch.getText() == null || txtSearch.getText().trim().equals("")){
            return;
        }
        // 搜索标题并且发布事件，刷新列表。
        List<Article> list = articleService.searchArticleByTitle(txtSearch.getText());
        ArticleListRefreshEvent event = new ArticleListRefreshEvent(list);
        config.publishEvent(event);
        txtSearch.setText("");
    }

    @PostConstruct
    public void initTypeTree(){
        root.set(typeService.getTypes());
        GUIState.getStage().setOnCloseRequest(e->{
            if(UIUtil.isClassical() && !UIUtil.isUseFloat()){
                Platform.exit();
                System.exit(0);
            }
        });
    }

    @EventListener
    public void onRefreshType(TypeRefreshEvent event){
        root.set(typeService.getTypes());
    }

}
