package org.swdc.note.ui.controllers;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.fxmisc.richtext.CodeArea;
import org.slf4j.Logger;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.*;
import org.swdc.note.core.files.SingleStorage;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.component.MDRichTextUtils;
import org.swdc.note.ui.component.RectPopover;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;
import org.swdc.note.ui.view.ArticleEditorView;
import org.swdc.note.ui.view.EditorContentView;
import org.swdc.note.ui.view.UIUtils;
import org.swdc.note.ui.view.dialogs.ImagesView;
import org.swdc.note.ui.view.dialogs.TypeSelectView;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.*;

import static org.swdc.note.ui.view.UIUtils.fxViewByView;


public class ArticleEditorController extends ViewController<ArticleEditorView> {

    @FXML
    private TabPane articlesTab;

    @Inject
    private Logger logger;

    @Inject
    private ArticleService articleService = null;


    @FXML
    private void onToolBold() {
        Tab select = articlesTab.getSelectionModel().getSelectedItem();
        if (select == null) {
            return;
        }
        EditorContentView viewEditor = fxViewByView(select.getContent(), EditorContentView.class);
        CodeArea codeArea = viewEditor.getCodeArea();

        String sel = codeArea.getSelectedText();
        IndexRange range = codeArea.getSelection();
        if(sel==null||sel.equals("")){
            // 获取光标位置
            IndexRange rgCurr = new IndexRange(codeArea.getCaretPosition(),codeArea.getCaretPosition());
            // 插入内容
            codeArea.replaceText(rgCurr,"**内容写在这里**");
            return;
        }
        sel = MDRichTextUtils.reduceDesc(sel,"**");
        codeArea.replaceText(range.getStart(),range.getEnd(),sel);
    }

    @FXML
    private void onToolItalic() {
        Tab select = articlesTab.getSelectionModel().getSelectedItem();
        if (select == null) {
            return;
        }
        EditorContentView viewEditor = fxViewByView(select.getContent(), EditorContentView.class);
        CodeArea codeArea = viewEditor.getCodeArea();

        String sel = codeArea.getSelectedText();
        IndexRange range = codeArea.getSelection();
        if(sel==null||sel.equals("")){
            IndexRange rgCurr = new IndexRange(codeArea.getCaretPosition(),codeArea.getCaretPosition());
            codeArea.replaceText(rgCurr,"*内容写在这里*");
            return;
        }
        sel = MDRichTextUtils.reduceDesc(sel,"*");
        codeArea.replaceText(range.getStart(),range.getEnd(),sel);
    }

    @FXML
    private void onToolImage() {
        Tab select = articlesTab.getSelectionModel().getSelectedItem();
        if (select == null) {
            return;
        }
        EditorContentView viewEditor = fxViewByView(select.getContent(), EditorContentView.class);
        ImagesView imagesView = viewEditor.getImagesView();
        viewEditor.getImagesView().show();
        CodeArea area = viewEditor.getCodeArea();
        Optional.ofNullable(imagesView.getSelectedImageName()).ifPresent(img->{
            IndexRange rgCurr = new IndexRange(area.getCaretPosition(),area.getCaretPosition());
            area.replaceText(rgCurr,"![输入简介]["+img+"]");
        });
        imagesView.clear();
    }

    @FXML
    private void onToolTable() {
        Tab select = articlesTab.getSelectionModel().getSelectedItem();
        if (select == null) {
            return;
        }
        EditorContentView viewEditor =fxViewByView(select.getContent(), EditorContentView.class);
        CodeArea codeArea = viewEditor.getCodeArea();

        ArticleEditorView view = getView();

        Button btnTable = view.findById("table");
        RectPopover popover = view.getTablePopover();

        popover.show(btnTable);
        popover.onSelected(rectResult -> {
            if (rectResult == null) {
                return;
            }
            String table = "";
            for (int i = 0;i < rectResult.getxCount() + 1; i++){
                for (int j = 0;j < rectResult.getyCount();j++){
                    if(i!=1){
                        table = table + "| <内容> ";
                    }else {
                        table = table + "|:-----:";
                    }
                    if(j+1 == rectResult.getyCount()){
                        table = table + "|";
                    }
                }
                table = table + "\n";
            }
            IndexRange rgCurr = new IndexRange(codeArea.getCaretPosition(),codeArea.getCaretPosition());
            codeArea.replaceText(rgCurr,"\n\n"+table);
        });
    }

    @FXML
    private void onToolListOl(){
        Tab select = articlesTab.getSelectionModel().getSelectedItem();
        if (select == null) {
            return;
        }
        EditorContentView viewEditor = fxViewByView(select.getContent(), EditorContentView.class);
        CodeArea codeArea = viewEditor.getCodeArea();
        int idx = 1;
        String txt = codeArea.getSelectedText();
        if(txt == null || txt.equals("")){
            IndexRange rgCurr = new IndexRange(codeArea.getCaretPosition(),codeArea.getCaretPosition());
            codeArea.replaceText(rgCurr," 1. 列表项a \n 2. 列表项b \n 3. 列表项c");
            return;
        }
        List<String> list = Arrays.asList(txt.split("\n"));
        StringBuilder sb = new StringBuilder();
        for (String str:list) {
            sb.append("\n " )
                    .append((idx++))
                    .append(". ")
                    .append(str);
        }
        codeArea.replaceText(codeArea.getSelection(),sb.toString());
    }

    @FXML
    private void onToolListUl() {
        Tab select = articlesTab.getSelectionModel().getSelectedItem();
        if (select == null) {
            return;
        }
        EditorContentView viewEditor = fxViewByView(select.getContent(), EditorContentView.class);
        CodeArea codeArea = viewEditor.getCodeArea();
        String txt = codeArea.getSelectedText();
        if(txt == null || txt.equals("")){
            IndexRange rgCurr = new IndexRange(codeArea.getCaretPosition(),codeArea.getCaretPosition());
            codeArea.replaceText(rgCurr," - 列表项a \n - 列表项b \n - 列表项c");
            return;
        }
        List<String> list = Arrays.asList(txt.split("\n"));
        StringBuilder sb = new StringBuilder();
        list.forEach(str->{
            if(str.trim().equals("")){
                return;
            }
            sb.append("\n - " ).append(str);
        });
        codeArea.replaceText(codeArea.getSelection(),sb.toString());
    }

    @FXML
    private void onToolTodo() {
        Tab select = articlesTab.getSelectionModel().getSelectedItem();
        if (select == null) {
            return;
        }
        EditorContentView viewEditor = fxViewByView(select.getContent(), EditorContentView.class);
        CodeArea codeArea = viewEditor.getCodeArea();
        String sel = codeArea.getSelectedText();
        IndexRange range = codeArea.getSelection();
        if (sel == null|| sel.equals("")){
            IndexRange rgCurr = new IndexRange(codeArea.getCaretPosition(),codeArea.getCaretPosition());
            codeArea.replaceText(rgCurr,"\n * [ ] 计划列表");
            return;
        }
        sel = "\n * [ ] "+sel+"\n";
        codeArea.replaceText(range.getStart(),range.getEnd(),sel);
    }

    @FXML
    private void onToolCode() {
        Tab select = articlesTab.getSelectionModel().getSelectedItem();
        if (select == null) {
            return;
        }
        EditorContentView viewEditor = fxViewByView(select.getContent(), EditorContentView.class);
        CodeArea codeArea = viewEditor.getCodeArea();
        String sel = codeArea.getSelectedText();
        IndexRange range = codeArea.getSelection();
        if (sel == null|| sel.equals("")){
            IndexRange rgCurr = new IndexRange(codeArea.getCaretPosition(),codeArea.getCaretPosition());
            codeArea.replaceText(rgCurr,"\n```language\n \\\\ 在这里编写代码 \n ```");
            return;
        }
        sel = "```language\n"+sel+"\n```";
        codeArea.replaceText(range.getStart(),range.getEnd(),sel);
    }

    @FXML
    private void onToolThrow() {
        Tab select = articlesTab.getSelectionModel().getSelectedItem();
        if (select == null) {
            return;
        }
        EditorContentView viewEditor = fxViewByView(select.getContent(), EditorContentView.class);
        CodeArea codeArea = viewEditor.getCodeArea();

        String sel = codeArea.getSelectedText();
        IndexRange range = codeArea.getSelection();
        if(sel==null||sel.equals("")){
            IndexRange rgCurr = new IndexRange(codeArea.getCaretPosition(),codeArea.getCaretPosition());
            codeArea.replaceText(rgCurr,"~~内容写在这里~~");
            return;
        }
        sel = MDRichTextUtils.reduceDesc(sel,"~~");
        codeArea.replaceText(range.getStart(),range.getEnd(),sel);
    }

    @FXML
    private void onToolQuite() {
        Tab select = articlesTab.getSelectionModel().getSelectedItem();
        if (select == null) {
            return;
        }
        EditorContentView viewEditor = fxViewByView(select.getContent(), EditorContentView.class);
        CodeArea codeArea = viewEditor.getCodeArea();

        String text = codeArea.getSelectedText();
        if(text==null||text.equals("")){
            IndexRange rgCurr = new IndexRange(codeArea.getCaretPosition(),codeArea.getCaretPosition());
            codeArea.replaceText(rgCurr,"\n> 输入引用的内容\n");
            return;
        }
        StringBuilder sb = new StringBuilder("\n");
        List<String> list = Arrays.asList(text.split("\n"));
        for (String str:  list) {
            sb.append("\n> "+str );
        }
        codeArea.replaceText(codeArea.getSelection(),sb.toString());
    }

    @FXML
    private void onToolSave() {
        ArticleEditorView view = getView();
        Article article = view.getEditingArticle();
        Tab select = articlesTab.getSelectionModel().getSelectedItem();
        if (select == null) {
            return;
        }
        EditorContentView editor = fxViewByView(select.getContent(), EditorContentView.class);
        String source = editor.getCodeArea().getText();
        ArticleResource resource = new ArticleResource();
        if (article.getType() == null && article.getSingleStore() == null) {
            view.alert("提示","请设置分类，然后重新保存。", Alert.AlertType.ERROR)
                    .showAndWait();
            return;
        }
        Map<String, ByteBuffer> images = editor.getImagesView().getImages();
        Map<String, byte[]> imageData = new HashMap<>(images.size());
        for (Map.Entry<String,ByteBuffer> item :images.entrySet()) {
            imageData.put(item.getKey(), item.getValue().array());
        }
        resource.setImages(imageData);
        ArticleContent content = articleService.getContentOf(article);
        if (content == null) {
            content = new ArticleContent();
        }
        content.setSource(source);
        content.setImages(imageData);
        try {
            if (article.getSingleStore() != null) {
                // 文档直接从文件打开，那么保存到文件。
                SingleStorage storage = articleService.getSingleStoreBy(article.getSingleStore());
                article.setContent(content);
                storage.save(article,new File(article.getFullPath()));
                editor.setSaved();
                getView().emit(new RefreshEvent(article, getView(), RefreshType.UPDATE));
                select.setText(article.getTitle());
                UIUtils.notification("文档《" + article.getTitle() + "》 保存成功！");
                return;
            }
            Article saved = articleService.saveArticle(article, content, ArticleEditorType.MarkdownEditor);
            if(saved == null) {
                view.alert("提示", "保存失败, 请填写必要信息。", Alert.AlertType.ERROR)
                        .showAndWait();
            } else {
                editor.setSaved();
                select.setText(article.getTitle());
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
    private void changeType() {
        TypeSelectView selectView = getView().getView(TypeSelectView.class);
        selectView.show();
        ArticleType type = selectView.getSelected();
        if (type == null) {
            return;
        }
        ArticleEditorView view = getView();
        Article article = view.getEditingArticle();
        article.setType(type);
        view.refresh();
    }

    @FXML
    public void createArticle() {
        Article article = new Article();
        article.setTitle("未命名");
        article.setCreateDate(new Date());
        ArticleEditorView view = getView();
        view.addArticle(article);
    }

}
