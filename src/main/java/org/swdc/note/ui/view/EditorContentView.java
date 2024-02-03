package org.swdc.note.ui.view;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.slf4j.Logger;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.Fontawsome5Service;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.config.AppConfig;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.render.HTMLRender;
import org.swdc.note.ui.component.ContentHelper;
import org.swdc.note.ui.view.dialogs.ImagesView;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
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

    @Inject
    private Fontawsome5Service fontawsome5Service;

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

    private ContextMenu editorMenu = null;

    private boolean hasSaved = false;

    private boolean versionsVisible = false;

    public void setSaved() {
        hasSaved = true;
    }

    public void setChanged() {
        hasSaved = false;
    }

    public boolean hasSaved() {
        return hasSaved;
    }


    @PostConstruct
    public void initialize() {
        BorderPane borderPane = findById("editor");
        CodeArea codeArea = new CodeArea();
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
                    .toList();
            list.addAll(imagesKeyWord);
        });

        Button arrowButton = findById("sideArrow");
        arrowButton.setPadding(new Insets(4));
        arrowButton.setFont(fontawsome5Service.getSolidFont(FontSize.SMALL));
        arrowButton.setText(fontawsome5Service.getFontIcon("angle-right"));
        toggleVersionPane(this.versionsVisible);
        arrowButton.setOnAction(e -> {
            this.versionsVisible = !this.versionsVisible;
            toggleVersionPane(versionsVisible);
            arrowButton.setText(fontawsome5Service.getFontIcon(versionsVisible ? "angle-left" : "angle-right"));
        });

        TableColumn<ArticleContent,Integer> colVersion = findById("colVersionId");
        colVersion.setCellValueFactory(new PropertyValueFactory<>("version"));
        TableColumn<ArticleContent,String> colVersionDate = findById("colVersionDate");
        colVersionDate.setCellValueFactory(new PropertyValueFactory<>("updateDate"));

        TableView<ArticleContent> contentTableView = findById("versionTable");
        contentTableView.setOnMouseClicked(e -> {
            ArticleContent content = contentTableView.getSelectionModel().getSelectedItem();
            if (content == null || e.getClickCount() < 2) {
                return;
            }
            Alert alert = alert("提示","的确要回滚到此版本吗？如果存在当前尚未保存的更改将会全部丢失", Alert.AlertType.CONFIRMATION);
            alert.showAndWait().ifPresent(t -> {
                if (t.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    loadArticleData(content);
                }
            });
        });

        Button latestVersion = findById("latestVersion");
        latestVersion.setOnAction(e -> {
            Alert alert = alert("提示","的确要返回最新版本吗？如果存在当前尚未保存的更改将会全部丢失", Alert.AlertType.CONFIRMATION);
            alert.showAndWait().ifPresent(t -> {
                if (t.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    latestVersion();
                }
            });
        });
    }

    private int extractVersion(ArticleContent content) {
        return content.getVersion() == null ? 0 : content.getVersion();
    }

    public void latestVersion() {
        TableView<ArticleContent> contentTableView = findById("versionTable");
        if (contentTableView.getItems().isEmpty()) {
            return;
        }
        contentTableView.getItems().stream().sorted((cA,cB) ->
                extractVersion(cB) - extractVersion(cA)
        ).findFirst().ifPresent(this::loadArticleData);
    }

    public void setVersions(List<ArticleContent> versions) {
        TableView<ArticleContent> versionTable = findById("versionTable");
        versionTable.getItems().clear();
        versionTable.getItems().addAll(versions);
    }

    public void loadArticleData(ArticleContent content) {
        imagesView.getImages().clear();
        codeArea.clear();

        Map<String,byte[]> resource = content.getImages();
        if (resource != null) {
            for (Map.Entry<String, byte[]> ent : resource.entrySet()) {
                imagesView.addImage(ent.getKey(), ent.getValue());
            }
        }

        String source = content.getSource();
        if(source != null) {
            codeArea.appendText(source);
        }

        setSaved();
        codeArea.getUndoManager().forgetHistory();
    }

    public void toggleVersionPane(boolean visible) {
        StackPane versionPane = findById("versions");
        VBox versionVBox = findById("versionVBox");
        versionVBox.setPadding(new Insets(0));
        if (visible) {
            versionPane.setPrefWidth(200);
        } else {
            versionPane.setPrefWidth(1);
        }
        versionVBox.setVisible(visible);
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
