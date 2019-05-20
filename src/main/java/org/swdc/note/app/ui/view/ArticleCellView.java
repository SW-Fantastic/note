package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.event.*;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Optional;

/**
 * 文档列表的单元格的视图
 * prototype模式，应该使用UIConfig获取
 */
@FXMLView("/view/artleCell.fxml")
@Scope(value = "prototype")
public class ArticleCellView extends AbstractFxmlView{

    @Autowired
    private UIConfig config;

    private Article article;

    @PostConstruct
    protected void initUI(){
        Button btnIcon = (Button)getView().lookup("#icon");
        btnIcon.setFont(UIConfig.getFontIconSmall());
        btnIcon.setText(String.valueOf(UIConfig.getAwesomeMap().get("file")));
        btnIcon.setVisible(true);
        VBox vbx = (VBox)getView().lookup("#bg");
        vbx.setVisible(true);
        Label lblArrow = (Label)getView().lookup("#arrow");
        lblArrow.setText(String.valueOf(UIConfig.getAwesomeMap().get("caret_left")));
        lblArrow.setFont(UIConfig.getFontIconLarge());
        Button btnRead = (Button) getView().lookup("#read");
        btnRead.setTextFill(Color.DARKGREEN);
        btnRead.setFont(UIConfig.getFontIconSmall());
        btnRead.setText(String.valueOf(UIConfig.getAwesomeMap().get("folder_open")));
        btnRead.setOnAction(e->{
            if(article !=null){
                // 发出事件，通知其他组件打开此文档
                config.publishEvent(new ArticleOpenEvent(article));
            }
        });
        Button btnDel = (Button)getView().lookup("#delete");
        btnDel.setTextFill(Color.DARKRED);
        btnDel.setFont(UIConfig.getFontIconSmall());
        btnDel.setText(String.valueOf(UIConfig.getAwesomeMap().get("trash")));
        btnDel.setOnAction(e->{
            if (article != null){
                Optional<ButtonType> result = UIUtil.showAlertDialog("你确实要删除《"+ article.getTitle()+"》吗？","删除", Alert.AlertType.CONFIRMATION, config);
                result.ifPresent(btnType->{
                    if(btnType.equals(ButtonType.OK)){
                        // 发送删除事件，通知controller删除此文档
                        config.publishEvent(new DeleteEvent(article));
                    }
                });
            }
        });
        Button btnEdit = (Button)getView().lookup("#edit");
        btnEdit.setTextFill(Color.DARKBLUE);
        btnEdit.setFont(UIConfig.getFontIconSmall());
        btnEdit.setText(String.valueOf(UIConfig.getAwesomeMap().get("pencil")));
        btnEdit.setOnAction(e->{
            if(article != null){
                // 发出事件，通知其他组件转到编辑状态
                config.publishEvent(new ArticleEditEvent(article));
            }
        });

        Button btnOut = (Button)getView().lookup("#export");
        btnOut.setTextFill(Color.FORESTGREEN);
        btnOut.setFont(UIConfig.getFontIconSmall());
        btnOut.setText(String.valueOf(UIConfig.getAwesomeMap().get("sign_out")));
        btnOut.setOnAction(e->{
            if(article != null){
                // 发送导出事件，要求导出文档。
                ExportEvent exportEvent = new ExportEvent(article);
                config.publishEvent(exportEvent);
            }
        });
    }

    public void setArticle(Article article){
        this.article = article;
        Button btnIcon = (Button)getView().lookup("#icon");
        VBox vbx = (VBox)getView().lookup("#bg");
        Label lblArrow = (Label)getView().lookup("#arrow");
        Label lblTitle = (Label)getView().lookup("#title");
        Label lblDate = (Label)getView().lookup("#date");
        HBox hbx = (HBox)getView().lookup("#editPane");
        if(article == null){
           btnIcon.setVisible(false);
            vbx.setVisible(false);
            lblArrow.setVisible(false);
            hbx.setVisible(false);
            lblTitle.setText("");
            lblDate.setText("");
            return;
        }
        lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(article.getCreatedDate()));
        lblTitle.setText(article.getTitle());
        btnIcon.setVisible(true);
        vbx.setVisible(true);
        lblArrow.setVisible(true);
        hbx.setVisible(true);
    }

}
