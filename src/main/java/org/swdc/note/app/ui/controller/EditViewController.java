package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleContext;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.event.ArticleEditEvent;
import org.swdc.note.app.event.ResetEvent;
import org.swdc.note.app.service.ArticleService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.MessageView;
import org.swdc.note.app.ui.view.StartEditView;
import org.swdc.note.app.ui.view.dialogs.TypeDialog;
import org.swdc.note.app.util.DataUtil;
import org.swdc.note.app.util.UIUtil;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * 文档编辑视图控制器.
 */
@FXMLController
public class EditViewController implements Initializable{

    private ArticleType currType;
    private Article article;

    @Autowired
    private UIConfig config;

    @Autowired
    private MessageView messageView;

    @FXML
    private TextField txtType;

    @FXML
    private TextField txtTitle;

    @Autowired
    private TypeDialog typeDialog;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private StartEditView editView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void selType() {
        Stage stg = typeDialog.getStage();
        if(editView.getStage() != null && stg.getOwner() != editView.getStage()){
            stg.initOwner(editView.getStage());
        }
        if(stg.isShowing()){
            stg.requestFocus();
        }else{
            stg.showAndWait();
        }
        this.currType = typeDialog.getArticleType();
        if(currType != null){
            txtType.setText(currType.getName());
        }
    }

    @FXML
    public void saveArticle(){

        if(currType == null){
            UIUtil.showAlertDialog("请选择分类。", "提示", Alert.AlertType.ERROR, config);
            return;
        }
        if(txtTitle.getText() == null || txtTitle.getText().equals("")){
            UIUtil.showAlertDialog("请输入标题。", "提示", Alert.AlertType.ERROR, config);
            return;
        }
        // 封装数据
        Article articleCurr = new Article();
        ArticleContext context = new ArticleContext();
        if(article != null){
            // 有artle对象，应该是在修改，先复制以前的数据
            ArticleContext contextOld = articleService.loadContext(article);
            DataUtil.updateProperties(article, articleCurr);
            DataUtil.updateProperties(contextOld,context);
        }
        // 写入新数据
        articleCurr.setCreatedDate(new Date());
        articleCurr.setType(currType);
        articleCurr.setTitle(txtTitle.getText());
        context.setContent(editView.getDocument());
        context.setImageRes(editView.getImageRes());
        this.article = articleService.saveArticle(articleCurr,context);
        editView.setSaved(true);
        messageView.setMessage("文档：" + txtTitle.getText() + "  已经保存。");
        Notifications.create()
                .hideCloseButton()
                .graphic(messageView.getView())
                .position(Pos.CENTER)
                .owner(GUIState.getStage())
                .hideAfter(new Duration(1200))
                .show();
    }

    /**
     * 处理文档的编辑请求事件。
     * @param event 编辑事件
     */
    @EventListener
    public void onArticleEdit(ArticleEditEvent event){
        Article article = event.getSource();
        if (article.getId() != null) {
            this.article = article;
        }
        this.txtTitle.setText(article.getTitle());
        ArticleContext context;
        if (!event.isContextFilled()) {
            context = articleService.loadContext(article);
            this.currType = article.getType();
            this.txtType.setText(currType.getName());
        } else {
            context = article.getContext();
        }
        editView.setContext(context.getContent());
        editView.setSaved(true);
        if(editView.getStage()!=null){
            Platform.runLater(()->{
                if(editView.getStage().isShowing()){
                    editView.getStage().requestFocus();
                }else{
                    editView.getStage().show();
                }
            });
        }
    }

    @EventListener
    public void onReset(ResetEvent resetEvent){
        if(resetEvent.getSource().equals(StartEditView.class)||resetEvent.getSource() == null){
            this.editView.reset();
            this.txtType.setText(null);
            this.currType = null;
            this.article = null;
            this.txtTitle.setText(null);
        }
    }

}
