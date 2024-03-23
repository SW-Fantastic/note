package org.swdc.note.ui.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.slf4j.Logger;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.*;
import org.swdc.note.core.files.SingleStorage;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.component.blocks.BlockData;
import org.swdc.note.ui.component.blocks.ImageBlock;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;
import org.swdc.note.ui.view.*;
import org.swdc.note.ui.view.dialogs.TypeSelectView;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.*;

import static org.swdc.note.ui.view.UIUtils.fxViewByView;

public class ArticleBlockEditorController extends ViewController<ArticleBlockEditorView> {

    @Inject
    private ArticleService articleService = null;

    @Inject
    private Logger logger;

    @FXML
    private TabPane articlesTab;

    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {

    }

    public void saveArticle(Tab tab, Article target) {
        if (tab == null || target == null) {
            return;
        }
        ArticleBlockEditorView view = getView();
        Article article = view.getArticle(tab);
        EditorBlockedContentView editor = fxViewByView(tab.getContent(), EditorBlockedContentView.class);
        List<BlockData> source = editor.getSource();
        if (article.getType() == null && article.getSingleStore() == null) {
            view.alert("提示","请设置分类，然后重新保存。", Alert.AlertType.ERROR)
                    .showAndWait();
            return;
        }
        ArticleContent content = articleService.getContentOf(article);
        if (content == null) {
            content = new ArticleContent();
        }
        Map<String,byte[]> images = new HashMap<>();
        for (BlockData data: source) {
            if(ImageBlock.class.getName().equals(data.getType())) {
                String binary = data.getSource();
                Map<String,String> header = (Map<String, String>) data.getContent();
                images.put(header.get("name"), Base64.getDecoder().decode(binary));
                data.setSource("");
            }
        }
        try {

            ObjectMapper mapper = new ObjectMapper();
            String serialized = mapper.writeValueAsString(source);
            content.setSource(serialized);
            content.setImages(images);

            if (article.getSingleStore() != null) {
                // 文档直接从文件打开，那么保存到文件。
                SingleStorage storage = articleService.getSingleStoreBy(article.getSingleStore());
                article.setContent(content);
                storage.save(article,new File(article.getFullPath()));
                editor.setChanged(false);
                getView().emit(new RefreshEvent(article, getView(), RefreshType.UPDATE));
                tab.setText(article.getTitle());
                UIUtils.notification("文档《" + article.getTitle() + "》 保存成功！");
                return;
            }
            Article saved = articleService.saveArticle(article, content, ArticleEditorType.BlockEditor);
            if(saved == null) {
                view.alert("提示", "保存失败, 请填写必要信息。", Alert.AlertType.ERROR)
                        .showAndWait();
            } else {
                editor.setChanged(false);
                tab.setText(article.getTitle());
                UIUtils.notification("文档《" + article.getTitle() + "》 保存成功！");
                this.getView().emit(new RefreshEvent(article,getView(), RefreshType.UPDATE));
            }
        } catch (Exception e) {
            PrintWriter writer = new PrintWriter(new StringWriter());
            e.printStackTrace(writer);
            view.alert("提示", "保存失败: 存储遇到异常 - \n" + writer, Alert.AlertType.ERROR)
                    .showAndWait();
            logger.error("fail to save article ", e);
        }
    }

    @FXML
    private void onSave() {
        ArticleBlockEditorView view = getView();
        Article article = view.getEditingArticle();
        Tab select = articlesTab.getSelectionModel().getSelectedItem();
        if (select == null) {
            return;
        }
        saveArticle(select,article);
    }

    @FXML
    private void changeType() {
        TypeSelectView selectView = getView().getView(TypeSelectView.class);
        selectView.show();
        ArticleType type = selectView.getSelected();
        if (type == null) {
            return;
        }
        ArticleBlockEditorView view = getView();
        Article article = view.getEditingArticle();
        article.setType(type);
        view.refresh();
    }

}
