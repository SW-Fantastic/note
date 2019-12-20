package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleContext;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.event.ArticleOpenEvent;
import org.swdc.note.app.event.ExportEvent;
import org.swdc.note.app.event.ResetEvent;
import org.swdc.note.app.file.Formatter;
import org.swdc.note.app.service.ArticleService;
import org.swdc.note.app.service.FormatterService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.MessageView;
import org.swdc.note.app.ui.view.StartReadView;
import org.swdc.note.app.ui.view.dialogs.ArticleSetDialog;
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
    private FormatterService formatterService;

    @Autowired
    private StartReadView readView;

    @Autowired
    private MessageView messageView;

    @Autowired
    private UIConfig config;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private TypeDialog typeDialog;

    @Autowired
    private ArticleSetDialog articleSetDialog;

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
        readView.getWebView().getBrowser().loadHTML(articleService.renderHTML(context));
        txtTitle.setText(article.getTitle());
        lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(article.getCreatedDate()));
        btnExport.setVisible(true);
        btnImport.setVisible(false);
    }

    @FXML
    protected void onOpen(){
        List<FileChooser.ExtensionFilter> list = new ArrayList<>();
        List<Formatter> formatters = formatterService.getAllFormatters();

        for (Formatter formatter : formatters) {
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(formatter.getFormatName(), "*." + formatter.getFormatExtension());
            list.add(filter);
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开");
        fileChooser.getExtensionFilters().addAll(list);
        File file = fileChooser.showOpenDialog(GUIState.getStage());

        if (file == null) {
            return;
        }

        String[] nameItems = file.getName().split("[.]");
        String extension = nameItems.length > 1 ? nameItems[nameItems.length - 1] : "";

        Formatter formatter = formatterService.getDocumentFormatterByExtension(extension,false);
        if (formatter != null) {
            // 单个数据文件打开
            Article article = (Article) formatter.readDocument(file);
            ArticleContext context = article.getContext();
            readView.getWebView().getBrowser().loadHTML(articleService.renderHTML(context));
            txtTitle.setText(article.getTitle());
            lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(article.getCreatedDate()));
            this.article = article;
            btnExport.setVisible(false);
            btnImport.setVisible(true);
        }

        formatter = formatterService.getDocumentFormatterByExtension(extension, true);
        if (formatter != null) {
            ArticleType articleType = (ArticleType)formatter.readDocument(file);
            articleSetDialog.setType(articleType);
            articleSetDialog.show();
        }
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

    @FXML
    protected void nextArticle() {
        this.pageAction(false);
    }

    @FXML
    protected void prevArticle() {
        this.pageAction(true);
    }

    @EventListener
    protected void onReset(ResetEvent event){
        if(event.getSource().equals(StartReadView.class) || event.getSource() == null){
            if(readView.getStage()!=null && readView.getStage().isShowing()){
                return;
            }
            this.article = null;
            readView.getWebView().getBrowser().loadHTML("");
            lblDate.setText("");
            txtTitle.setText("");
        }
    }

    private void pageAction (boolean turnPrev) {
        boolean alert = false;
        if (this.article == null) {
            messageView.setMessage("你没有打开任何文档。");
            alert = true;
        } else {
            Article article = null;
            if (!turnPrev) {
                article = articleService.nextArticleOnType(this.article);
            } else {
                article = articleService.prevArticleOnType(this.article);
            }
            if (article != null) {
                config.publishEvent(new ArticleOpenEvent(article));
            } else {
                messageView.setMessage("此分类没有更多文档了。");
                alert = true;
            }
        }
        if (alert) {
            Notifications.create()
                    .hideCloseButton()
                    .graphic(messageView.getView())
                    .position(Pos.CENTER)
                    .owner(GUIState.getStage())
                    .hideAfter(new Duration(1600))
                    .show();
        }
    }

}
