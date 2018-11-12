package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleContext;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.event.ArticleEditEvent;
import org.swdc.note.app.event.ResetEvent;
import org.swdc.note.app.service.ArticleService;
import org.swdc.note.app.ui.view.StartEditView;
import org.swdc.note.app.ui.view.dialogs.TypeDialog;
import org.swdc.note.app.util.DataUtil;

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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("提示");
        alert.initOwner(GUIState.getStage());
        if(currType == null){
            alert.setContentText("请先选择分类");
            alert.showAndWait();
            return;
        }
        if(txtTitle.getText() == null || txtTitle.getText().equals("")){
            alert.setContentText("请输入标题");
            alert.showAndWait();
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
        articleService.saveArticle(articleCurr,context);
    }

    /**
     * 处理文档的编辑请求事件。
     * @param event 编辑事件
     */
    @EventListener
    public void onArticleEdit(ArticleEditEvent event){
        Article article = event.getSource();
        this.article = article;
        this.currType = article.getType();
        this.txtType.setText(currType.getName());
        this.txtTitle.setText(article.getTitle());
        ArticleContext context = articleService.loadContext(article);
        editView.setContext(context.getContent());
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
