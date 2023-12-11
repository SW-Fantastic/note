package org.swdc.note.ui.view;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.config.AppConfig;
import org.swdc.note.ui.component.ContentHelper;
import org.swdc.note.ui.view.dialogs.ImagesView;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.swdc.note.ui.view.UIUtils.createMenuItem;

@View(stage = false,viewLocation = "views/main/EditorContentView.fxml",multiple = true)
public class EditorContentView extends AbstractView {

    private CodeArea codeArea;

    @Inject
    private ImagesView imagesView = null;

    @Inject
    private AppConfig appConfig;

    @Inject
    private Logger logger;

    private List<ContentHelper.KeyWord> keyWordsTipList = Arrays.asList(
            new ContentHelper.KeyWord("*", "无序列表"),
            new ContentHelper.KeyWord("**", "加粗"),
            new ContentHelper.KeyWord(">", "引用"),
            new ContentHelper.KeyWord("#","标题1"),
            new ContentHelper.KeyWord("##","标题2"),
            new ContentHelper.KeyWord("###","标题3"),
            new ContentHelper.KeyWord("####","标题4"),
            new ContentHelper.KeyWord("#####","标题5"),
            new ContentHelper.KeyWord("######","标题6"),
            new ContentHelper.KeyWord("$","公式"),
            new ContentHelper.KeyWord("- [ ] ", "待办列表"),
            new ContentHelper.KeyWord("- [x] ","待办列表"),
            new ContentHelper.KeyWord("* * * ", "分割线"),
            new ContentHelper.KeyWord("- - - ", "分割线"),
            new ContentHelper.KeyWord("~~","删除线"),
            new ContentHelper.KeyWord("[TOC]","目录"),
            new ContentHelper.KeyWord("![","插入图片", keyWord -> {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("打开图片");
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("图片文件","*.png","*.jpg", "*.jpeg","*.gif","*.bmp"));
                File image = chooser.showOpenDialog(this.getStage());
                if (image == null) {
                    return "";
                }
                try {
                    ImagesView imagesView = this.getImagesView();
                    imagesView.addImage(image.getName(), Files.readAllBytes(image.toPath()));
                    return "![description][" + image.getName() + "]";
                } catch (Exception e) {
                    logger.error("fail to read image file",e);
                    return "";
                }
            })
    );

    private ContentHelper helper;


    private boolean hasSaved = false;

    public void setSaved() {
        hasSaved = true;
    }

    public void setChanged() {
        hasSaved = false;
    }

    public boolean hasSaved() {
        return hasSaved;
    }

    private ContextMenu editorMenu = null;

    /**
     * 如果inputRequest为null，那么MAC系统将会出现无法输入中文的问题。
     */
    private static class InputMethodRequestsObject implements InputMethodRequests {

        private CodeArea area;

        InputMethodRequestsObject(CodeArea area){
            this.area = area;
        }

        @Override
        public Point2D getTextLocation(int offset) {
            Optional<Bounds> bounds = area.getCaretBounds();
            if (bounds.isPresent()){
                Bounds pos = bounds.get();
                return new Point2D(pos.getMinX(),pos.getMinY());
            }
            return new Point2D(0,0);
        }

        @Override
        public int getLocationOffset(int x, int y) {
            return 0;
        }

        @Override
        public void cancelLatestCommittedText() {

        }

        @Override
        public String getSelectedText() {
            return area.getSelectedText();
        }
    }

    @PostConstruct
    public void initialize() {
        BorderPane borderPane = findById("editor");
        CodeArea codeArea = new CodeArea();
        codeArea.setInputMethodRequests(new InputMethodRequestsObject(codeArea));
        codeArea.setOnInputMethodTextChanged(e->{
            if(e.getCommitted() != null){
                codeArea.insertText(codeArea.getCaretPosition(),e.getCommitted());
            }
        });
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.getStyleClass().add("code-area");

        borderPane.setCenter(new VirtualizedScrollPane<>(codeArea));
        this.codeArea = codeArea;
        this.createEditorMenu();
        this.codeArea.setContextMenu(this.editorMenu);
        helper = new ContentHelper();
        helper.setUpTooltip(codeArea,this,appConfig.getEnableAutoTip(),keyWordsTipList);
        helper.beforeShow(list -> {
            Set<String> images = this.getImagesView().getImages().keySet();
            List<ContentHelper.KeyWord> imagesKeyWord = images
                    .stream()
                    .map(s -> new ContentHelper.KeyWord("![","图片: " + s, self-> "![description][" + s + "]"))
                    .collect(Collectors.toList());
            list.addAll(imagesKeyWord);
        });

    }

    private void createEditorMenu() {
        this.editorMenu = new ContextMenu();
        List<MenuItem> menuItems = this.editorMenu.getItems();
        MenuItem undoItem = createMenuItem("撤销   ",this::onUndo,new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        menuItems.add(undoItem);

        MenuItem redoItem = createMenuItem("重做   ", this::onRedo, new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        menuItems.add(redoItem);
        codeArea.textProperty().addListener(e -> {
            if (codeArea.isUndoAvailable()) {
                undoItem.setDisable(false);
            } else {
                undoItem.setDisable(true);
            }
            if (codeArea.isRedoAvailable()) {
                redoItem.setDisable(false);
            } else {
                redoItem.setDisable(true);
            }
        });

        menuItems.add(new SeparatorMenuItem());
        menuItems.add(createMenuItem("剪切   ",this::onCut,new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN)));
        menuItems.add(createMenuItem("复制   ", this::onCopy, new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN)));
        menuItems.add(createMenuItem("粘贴   ", this::onPaste, new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN)));
        menuItems.add(createMenuItem("删除   ", this::onDelete, new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN)));
        menuItems.add(new SeparatorMenuItem());
        menuItems.add(createMenuItem("全选   ",this::onSelectAll,new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN)));
    }

    private void onCopy(ActionEvent event) {
       codeArea.copy();
    }

    private void onCut(ActionEvent event) {
        codeArea.cut();
    }

    private void onPaste(ActionEvent event) {
        codeArea.paste();
    }

    private void onUndo(ActionEvent event) {
        codeArea.undo();
    }

    private void onRedo(ActionEvent event) {
        codeArea.redo();
    }

    private void onDelete(ActionEvent event) {
        IndexRange range = codeArea.getSelection();
        if (range == null) {
            return;
        }
        codeArea.deleteText(range);
    }

    private void onSelectAll(ActionEvent event) {
        codeArea.selectAll();
    }

    public WebView getWebView() {
        return findById("wView");
    }

    public CodeArea getCodeArea() {
        return codeArea;
    }

    public ImagesView getImagesView() {
        return imagesView;
    }

    public ContentHelper getHelper() {
        return helper;
    }
}
