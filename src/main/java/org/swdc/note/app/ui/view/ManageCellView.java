package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.event.ArticleEditEvent;
import org.swdc.note.app.event.DeleteEvent;
import org.swdc.note.app.event.ExportEvent;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * Created by lenovo on 2018/11/4.
 */
@FXMLView("/view/classes/manageCell.fxml")
@Scope("prototype")
public class ManageCellView extends AbstractFxmlView {

    @Setter
    private Article article;

    @Autowired
    private UIConfig config;

    @PostConstruct
    protected void initUI(){
        Button btnDel = (Button)getView().lookup("#delete");
        btnDel.setTextFill(Color.DARKRED);
        btnDel.setFont(UIConfig.getFontIconSmall());
        btnDel.setText(String.valueOf(UIConfig.getAwesomeMap().get("trash")));
        btnDel.setOnAction(e->{
            if (article != null){
                Optional<ButtonType> result = UIUtil.showAlertDialog("你确实要删除《"+ article.getTitle()+"》吗？", "删除", Alert.AlertType.CONFIRMATION, config);
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

}
