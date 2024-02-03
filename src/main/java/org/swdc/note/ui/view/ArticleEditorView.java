package org.swdc.note.ui.view;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleEditorType;
import org.swdc.note.core.files.SingleStorage;
import org.swdc.note.core.render.HTMLRender;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.core.service.ContentService;
import org.swdc.note.ui.component.MDRichTextUtils;
import org.swdc.note.ui.component.RectPopover;
import org.swdc.note.ui.controllers.ArticleEditorController;
import org.swdc.note.ui.events.RefreshEvent;
import org.swdc.note.ui.events.RefreshType;

import java.io.File;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.swdc.note.ui.view.UIUtils.fxViewByView;


@View(title = "编辑",viewLocation = "views/main/ArticleEditorView.fxml",css = {
        "editor.css", "keywords.css"
})
public class ArticleEditorView extends AbstractView {

    private Map<Article, Tab> articleTabMap = new HashMap<>();

    private ObservableList<Tab> tabs = FXCollections.observableArrayList();

    @Inject
    private MaterialIconsService iconsService = null;

    @Inject
    private HTMLRender render = null;

    @Inject
    private ArticleService articleService = null;

    @Inject
    private ContentService contentService  = null;

    private RectPopover tablePopover;


    @PostConstruct
    public void initialize() {

        Stage stage = this.getStage();
        stage.setMinWidth(840);
        stage.setMinHeight(628);

        stage.setWidth(stage.getMinWidth());
        stage.setHeight(stage.getMinHeight());
        stage.setOnCloseRequest(this::closeRequest);

        tablePopover = new RectPopover();

        TabPane tabPane = findById("editorTab");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        Bindings.bindContentBidirectional(tabPane.getTabs(),tabs);
        tabPane.getSelectionModel().selectedItemProperty().addListener(this::onTabChange);

        this.initViewToolButton("create","add");
        this.initViewToolButton("save","save");
        this.initViewToolButton("throwLine", "format_strikethrough");
        this.initViewToolButton("listOl", "format_list_numbered");
        this.initViewToolButton("listUl", "format_list_bulleted");
        this.initViewToolButton("todo", "check");
        this.initViewToolButton("table", "border_all");
        this.initViewToolButton("code", "code");
        this.initViewToolButton("img", "image");
        this.initViewToolButton("bold", "format_bold");
        this.initViewToolButton("italic", "format_italic");
        this.initViewToolButton("quote","format_quote");
        MenuButton header = findById("header");
        header.setFont(iconsService.getFont(org.swdc.fx.font.FontSize.SMALL));
        header.setText(iconsService.getFontIcon("title"));
        header.getItems().forEach(this::initHeaderMenu);

        TextField txtTitle = findById("title");
        txtTitle.textProperty().addListener((observableValue, oldStr, newStr) -> {
            Tab select = tabPane.getSelectionModel().getSelectedItem();
            if (select == null) {
                return;
            }
            Map.Entry<Article, Tab> selectEntry = articleTabMap.entrySet()
                    .stream().filter(ent -> ent.getValue() == select)
                    .findFirst()
                    .orElse(null);
            EditorContentView contentView = fxViewByView(select.getContent(), EditorContentView.class);
            selectEntry.getKey().setTitle(newStr);
            select.setText((contentView.hasSaved() ? "" : "* ") + newStr);
        });
    }

    private void closeRequest(Event event) {
        event.consume();
        if (tablePopover.isShowing()) {
            tablePopover.hide(javafx.util.Duration.ZERO);
        }
        List<EditorContentView> editors = tabs.stream()
                .map(tab->tab.getContent().getUserData())
                .map(EditorContentView.class::cast)
                .collect(Collectors.toList());
        long unsaved = editors.stream().filter(v->!v.hasSaved()).count();
        if (unsaved > 0) {
            alert("关闭","继续关闭将会失去所有未保存内容，继续吗？", Alert.AlertType.CONFIRMATION)
                    .showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.OK) {
                            tabs.clear();
                            articleTabMap.clear();
                            editors.forEach(e -> e.getHelper().cancel());
                            this.hide();
                        }
                    });
        } else {
            editors.forEach(e -> e.getHelper().cancel());
            tabs.clear();
            articleTabMap.clear();
            this.hide();
        }
    }

    public void onTabChange(Observable observable, Tab oldVal, Tab newTab) {
        if (newTab == null) {
            return;
        }
        Map.Entry<Article, Tab> entry = articleTabMap.entrySet()
                .stream()
                .filter(ent -> ent.getValue() == newTab)
                .findFirst().orElse(null);
        if (entry == null) {
            return;
        }

        TextField txtTitle = findById("title");
        TextField txtType = findById("type");

        Button changeType = findById("changeType");

        Article article = entry.getKey();
        if (article.getSingleStore() != null) {
            changeType.setDisable(true);
        } else {
            changeType.setDisable(false);
        }
        txtTitle.setText(article.getTitle());
        if (article.getType() != null) {
            txtType.setText(article.getType().getName());
        } else {
            txtType.setText("");
        }
    }

    private void initViewToolButton(String id, String icon) {
        Button btn = findById(id);
        if (btn == null) {
            return;
        }
        btn.setPadding(new Insets(4,4,4,4));
        btn.setFont(iconsService.getFont(FontSize.MIDDLE_SMALL));
        btn.setText(iconsService.getFontIcon(icon));
    }

    private void initHeaderMenu(MenuItem item){
        int level = Integer.valueOf(item.getId().replace("h",""));
        item.setOnAction(act->{
            TabPane tabPane = findById("editorTab");
            Tab select = tabPane.getSelectionModel().getSelectedItem();
            if (select == null) {
                return;
            }
            EditorContentView viewEditor = fxViewByView(select.getContent(), EditorContentView.class);
            CodeArea code = viewEditor.getCodeArea();

            IndexRange range = code.getSelection();
            String sel = code.getSelectedText();
            if(sel==null||sel.equals("")){
                IndexRange rgCurr = new IndexRange(code.getCaretPosition(),code.getCaretPosition());
                for (int idx = 0 ;idx <level;idx++){
                    sel = sel + "#";
                }
                code.replaceText(rgCurr,"\n"+sel+" 这里是标题");
                return;
            }
            if(sel.contains("#")){
                sel = sel.replaceAll("[#]","");
                code.replaceText(range.getStart(),range.getEnd(),sel);
                return;
            }
            sel = "\n "+sel;
            for (int idx = 0 ;idx <level;idx++){
                sel = "#" + sel ;
            }
            code.replaceText(range.getStart(),range.getEnd(),sel);
            return;
        });
    }

    public Article getEditingArticle() {
        TabPane tabPane = findById("editorTab");
        Tab tab  = tabPane.getSelectionModel().getSelectedItem();
        if (tab == null) {
            return null;
        }
        Map.Entry<Article, Tab> entry = articleTabMap.entrySet()
                .stream()
                .filter(ent -> ent.getValue() == tab)
                .findFirst().orElse(null);
        return entry.getKey();
    }

    public Article getArticle(String articleId) {
        if (articleId == null) {
            return null;
        }
        Article select = articleTabMap
                .keySet()
                .stream()
                .filter(k -> articleId.equals(k.getId()))
                .findFirst()
                .orElse(null);
        return select;
    }


    @EventListener(type = RefreshEvent.class)
    public void onRefreshed(RefreshEvent event) {
        if (event.getArticle() != null) {
            Article hasOpened = getArticle(event.getArticle().getId());
            if (hasOpened != null) {
                Tab tab =  articleTabMap.get(hasOpened);
                EditorContentView editor = UIUtils.fxViewByView(tab.getContent(),EditorContentView.class);
                editor.setVersions(contentService.getVersions(hasOpened.getId()));
            }
        }
    }

    public Tab addArticle(Article article) {

        Article hasOpen = getArticle(article.getId());
        TabPane tabPane = findById("editorTab");
        if(hasOpen != null) {
            Tab tab =  articleTabMap.get(hasOpen);
            tabPane.getSelectionModel().select(tab);
            return tab;
        }
        Tab tab = new Tab();
        tab.setText(article.getTitle());
        tab.setClosable(true);
        EditorContentView editor = getView(EditorContentView.class);

        CodeArea codeArea = editor.getCodeArea();
        codeArea.setWrapText(true);
        codeArea.plainTextChanges().successionEnds(Duration.ofMillis(500))
                .subscribe(ignore -> codeArea.setStyleSpans(0, MDRichTextUtils.computeHighlighting(codeArea.getText())));

        ArticleContent content  = Optional.ofNullable(article.getContent())
                .orElse(articleService.getContentOf(article));
        if (content != null) {
            editor.loadArticleData(content);
        }

        String articleSource = render.render(codeArea.getText(),editor.getImagesView().getImages());
        String renderedContext = render.renderHTML(articleSource);

        WebView webView = editor.getWebView();
        webView.getEngine().loadContent(renderedContext);

        codeArea.textProperty().addListener(((observable, oldValue, newValue) ->{
            String source = render.render(codeArea.getText(),editor.getImagesView().getImages());
            String context = render.renderHTML(source);
            webView.getEngine().loadContent(context);
            editor.setChanged();
            tab.setText("* " + article.getTitle());
        }));

        webView.getEngine().getLoadWorker().stateProperty().addListener((observableValue, stateOld, stateNew) -> {
            if (stateNew == Worker.State.SUCCEEDED) {
                // 计算当前行
                int currLine = codeArea.getText().substring(0,codeArea.getCaretPosition()).split("\n").length;
                // 总行数
                int lines = codeArea.getText().split("\n").length;
                double scrollPos = 1;
                // 计算滚动位置
                if(lines > 0 && currLine>0){
                    scrollPos = ((double)currLine) / ((double) lines);
                }
                editor.getWebView().getEngine().executeScript("window.scrollTo(0, document.body.clientHeight * "+scrollPos+");");
            }
        });

        editor.setVersions(contentService.getVersions(article.getId()));

        BorderPane borderPane = (BorderPane) editor.getView();
        borderPane.setUserData(editor);
        tab.setContent(borderPane);
        articleTabMap.put(article, tab);
        tabs.add(tab);
        borderPane.prefHeightProperty().bind(tab.getTabPane().heightProperty().subtract(42));
        borderPane.prefWidthProperty().bind(tab.getTabPane().widthProperty());
        tab.setOnCloseRequest(e -> this.onTabClose(e, tab, article));

        tabPane.getSelectionModel().select(tab);
        codeArea.getUndoManager().forgetHistory();

        return tab;
    }

    public void refresh() {
        Article article = getEditingArticle();
        TextField txtTitle = findById("title");
        TextField txtType = findById("type");
        txtTitle.setText(article.getTitle());
        txtType.setText(article.getType().getName());
    }

    private void onTabClose(Event e, Tab tab, Article article) {
        e.consume();
        EditorContentView editor = fxViewByView(tab.getContent(), EditorContentView.class);
        if (editor.hasSaved()) {
            tabs.remove(tab);
            articleTabMap.remove(article);
            if (tabs.size() == 0) {
                getStage().close();
            }
            return;
        }
        this.alert("关闭","是否要保存《" + article.getTitle() + "》?", Alert.AlertType.CONFIRMATION)
                .showAndWait()
                .ifPresent(buttonType -> {
                    if (buttonType == ButtonType.OK) {
                        ArticleEditorController controller = getController();
                        controller.saveArticle(article,tab);
                    }
                    tabs.remove(tab);
                    articleTabMap.remove(article);
                    if (tabs.size() == 0) {
                        getStage().close();
                    }
                });
    }



    public static double getScreenX(Node node) {
        return  node.localToScreen(0,0).getX();
    }

    public static double getScreenY(Node node) {
        return node.localToScreen(0,0).getY();
    }


    public RectPopover getTablePopover() {
        return tablePopover;
    }
}
