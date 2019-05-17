package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleContext;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.event.ArticleOpenEvent;
import org.swdc.note.app.event.ExportEvent;
import org.swdc.note.app.event.ResetEvent;
import org.swdc.note.app.event.TypeImportEvent;
import org.swdc.note.app.file.FileFormatter;
import org.swdc.note.app.service.ArticleService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.StartReadView;
import org.swdc.note.app.ui.view.dialogs.TypeDialog;
import org.swdc.note.app.util.UIUtil;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 阅读视图的控制器。
 */
@FXMLController
public class ReadViewController implements Initializable {

    /**
     * 当前读取的实体
     */
    private Article article;

    @Autowired
    private StartReadView readView;

    @Autowired
    private UIConfig config;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private List<FileFormatter> formaters;

    @Autowired
    private TypeDialog typeDialog;

    @FXML
    protected TextField txtTitle;

    @FXML
    protected Label lblDate;

    @FXML
    protected Button btnImport;

    @FXML
    protected Button btnExport;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @EventListener
    public void onArticleOpen(ArticleOpenEvent e){
        Article article = e.getArticle();
        this.article = article;
        ArticleContext context = articleService.loadContext(article);
        readView.getWebView().getEngine().loadContent(articleService.compile(context));
        txtTitle.setText(article.getTitle());
        lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(article.getCreatedDate()));
        btnExport.setVisible(true);
        btnImport.setVisible(false);
    }

    @FXML
    protected void onOpen(){
        List<FileChooser.ExtensionFilter> list = new ArrayList<>();
        formaters.stream().filter(item->item.canRead()).map(item->item.getFilters()).forEach(list::addAll);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开");
        fileChooser.getExtensionFilters().addAll(list);
        File file = fileChooser.showOpenDialog(GUIState.getStage());
        FileChooser.ExtensionFilter filter = fileChooser.getSelectedExtensionFilter();
        formaters.stream().filter(item->item.getFilters().contains(filter)).findFirst().ifPresent(target->{
            Article article = target.processRead(file,Article.class);
            if(article != null){
                // 单个数据文件打开
                ArticleContext context = article.getContext();
                readView.getWebView().getEngine().loadContent(articleService.compile(context));
                txtTitle.setText(article.getTitle());
                lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(article.getCreatedDate()));
                this.article = article;
                btnExport.setVisible(false);
                btnImport.setVisible(true);
            }else {
                // 数据合并导入
                TypeImportEvent importEvent = new TypeImportEvent(file,target);
                config.publishEvent(importEvent);
            }
        });
    }

    @FXML
    protected void onImport(){
        if(article !=null){
            typeDialog.getStage().showAndWait();
            ArticleType type = typeDialog.getArticleType();
            if(type != null){
                article.setType(type);
                articleService.saveArticle(article, article.getContext());
                btnImport.setVisible(false);
                UIUtil.showAlertDialog("文档已经导入系统。", "提示", Alert.AlertType.INFORMATION, config);
            }
        }
    }

    @FXML
    protected void onExport(){
        if(this.article !=null){
            ExportEvent exportEvent = new ExportEvent(article);
            config.publishEvent(exportEvent);
        }
    }

    @EventListener
    protected void onReset(ResetEvent event){
        if(event.getSource().equals(StartReadView.class) || event.getSource() == null){
            if(readView.getStage()!=null && readView.getStage().isShowing()){
                return;
            }
            this.article = null;
            readView.getWebView().getEngine().loadContent("");
            lblDate.setText("");
            txtTitle.setText("");
        }
    }

}
