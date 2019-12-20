package org.swdc.note.app.ui.controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.event.DeleteEvent;
import org.swdc.note.app.event.ExportEvent;
import org.swdc.note.app.event.TypeImportEvent;
import org.swdc.note.app.file.Formatter;
import org.swdc.note.app.service.ArticleService;
import org.swdc.note.app.service.TypeService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.dialogs.ArticleSetDialog;
import org.swdc.note.app.ui.view.dialogs.ExportDialog;
import org.swdc.note.app.ui.view.dialogs.TypeDialog;
import org.swdc.note.app.util.UIUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;

/**
 * 数据通用控制器
 */
@Controller
public class CommonController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private ExportDialog exportDialog;

    @Autowired
    private TypeDialog typeDialog;

    @Autowired
    private UIConfig config;

    @Autowired
    private ArticleSetDialog articleSetDialog;

    /**
     * 处理导出事件
     * @param exportEvent 导出事件
     */
    @EventListener
    public void onExportArticle(ExportEvent exportEvent){
        Stage stage = exportDialog.getStage();
        exportDialog.initExport(exportEvent);
        if(stage.isShowing()){
            stage.requestFocus();
        }else{
            stage.show();
        }
    }
    /**
     * 处理文档删除事件
     * @param deleteEvent 删除事件
     */
    @EventListener
    public void onDeleteArticle(DeleteEvent deleteEvent){
        if(deleteEvent.isArtleDel()){
            articleService.deleteArticle(deleteEvent.getArtle());
        }
    }

    /**
     * 处理分类删除事件
     * @param deleteEvent 删除事件
     */
    @EventListener
    public void onTypeDelete(DeleteEvent deleteEvent){
        if(deleteEvent.isArtleTypeDel()){
            Optional<ButtonType> result =  UIUtil.showAlertDialog("删除分类，如果分类下含有其他数据，那么也会同时被删除，确定要这样做吗？", "提示", Alert.AlertType.CONFIRMATION, config);
            result.ifPresent(btnType->{
                if(btnType.equals(ButtonType.OK)){
                    if(!typeService.delType(deleteEvent.getArtleType(),false)){
                        UIUtil.showAlertDialog("此分类下含有其他数据，如果你依然需要删除，那么" +
                                        "包括子分类下（如果有子分类的话）的所有数据都将会被删除，依然要这样做吗？", "提示",
                                Alert.AlertType.CONFIRMATION, config).ifPresent(btn->{
                            if(btn.equals(ButtonType.OK)){
                                typeService.delType(deleteEvent.getArtleType(),true);
                            }
                        });
                    }
                }
            });
        }
    }

    @EventListener
    public void onTypeImport(TypeImportEvent importEvent){
        Stage stg = typeDialog.getStage();
        if(stg.isShowing()){
            stg.requestFocus();
        }else{
            stg.showAndWait();
        }
        ArticleType type = typeDialog.getArticleType();
        if(type == null){
            return;
        }
        ArticleType source = importEvent.getType();
        this.mountImportType(source, type);
    }

    private void mountImportType(ArticleType articleType, ArticleType parent) {

        if (parent.getId() == null) {
            return;
        }

        ArticleType type = new ArticleType();
        type.setParentType(parent);
        type.setName(articleType.getName());
        type = typeService.saveType(type);

        if (type == null) {
            return;
        }

        for (Article article : articleType.getArticles()) {
            Article created = new Article();
            created.setType(type);
            created.setTitle(article.getTitle());
            created.setCreatedDate(article.getCreatedDate());
            articleService.saveArticle(created, article.getContext());
        }

        if (articleType.getChildType() != null && articleType.getChildType().size() > 0) {
            for (ArticleType typeItem : articleType.getChildType()) {
                mountImportType(typeItem, type);
            }
        }

    }

}
