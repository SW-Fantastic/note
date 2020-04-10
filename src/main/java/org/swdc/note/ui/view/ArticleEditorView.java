package org.swdc.note.ui.view;

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
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.MaterialIconsService;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleResource;
import org.swdc.note.core.render.HTMLResolver;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.component.RectSelector;
import org.swdc.note.ui.events.RefreshEvent;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@View(title = "编辑", resizeable = true, background = true, style = {"editor", "keywords"})
public class ArticleEditorView extends FXView {

    private Map<Article, Tab> articleTabMap = new HashMap<>();
    private ObservableList<Tab> tabs = FXCollections.observableArrayList();

    @Aware
    private MaterialIconsService iconsService = null;

    @Aware
    private HTMLResolver render = null;

    @Aware
    private ArticleService articleService = null;

    private RectSelector rectSelector;

    private static final String[] KEYWORDS = new String[] {
            "toc","TOC","target"
    };
    // 匹配字符串
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    // 匹配高亮的单词
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    // 匹配小括号
    private static final String PAREN_PATTERN = "\\(|\\)";
    // 匹配markdown的#
    private static final String TITLE_PATTERN = "[#]{1,6}\\s\\S+\\n";
    // 匹配等号和减号（markdown的分割线）
    private static final String SP_PATTERN = "[=]+|[-]+|>\\s";
    // 匹配代码块
    private static final String CODE_PATTERN = "[`]{3}[\\S]*\\b|[`]{3}";
    // 匹配大括号
    private static final String BRACE_PATTERN = "\\{|\\}";
    // 匹配中括号
    private static final String BRACKET_PATTERN = "\\[|\\]";
    // 匹配列表
    private static final String LIST_PATTERN = "[0-9]([1-9])?[.]\\s|\\*[.]\\s";
    // 匹配表格
    private static final String TABLE_PATTERN = "(\\|([ \\S\\|]*\\|))";
    // 匹配任务列表
    private static final String TASK_PATTERN = "[-]\\s\\[([x]?|[\\s]?)\\]";
    // 加粗，斜体，删除线
    private static final String DESC_PATTERN = "([*]{2}[\\S]+[\\s\\S]?[*]{2})|([*][\\S]+[\\s\\S]?[*]|([~]{2})[\\S]+[\\s\\S]?[~]{2})|([`][\\S]+[\\s\\S]?[`])";
    // 匹配注释
    private static final String COMMENT_PATTERN = "([<][!][-]{2}[\\s\\S]*)|([-]{2}[>])";

    private static final String FUNCTEX_PATTERN ="\\$[^$]+\\$";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<CODE>" + CODE_PATTERN + ")"
                    + "|(?<LIST>" + LIST_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<TABLE>" + TABLE_PATTERN + ")"
                    + "|(?<TASK>" + TASK_PATTERN + ")"
                    + "|(?<SP>" + SP_PATTERN + ")"
                    + "|(?<DESC>" + DESC_PATTERN + ")"
                    + "|(?<FUNCTEX>" + FUNCTEX_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<TITLE>" + TITLE_PATTERN + ")");

    @Override
    public void initialize() {

        Stage stage = this.getStage();
        stage.setMinWidth(840);
        stage.setMinHeight(628);

        stage.setWidth(stage.getMinWidth());
        stage.setHeight(stage.getMinHeight());
        stage.setOnCloseRequest(this::closeRequest);

        rectSelector = new RectSelector();
        rectSelector.initOwner(this.getStage());

        TabPane tabPane = findById("editorTab");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        Bindings.bindContentBidirectional(tabPane.getTabs(),tabs);
        tabPane.getSelectionModel().selectedItemProperty().addListener(this::onTabChange);

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
        header.setFont(iconsService.getFont(FontSize.SMALL));
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
        Long unsaved = tabs.stream()
                .map(tab->tab.getContent().getUserData())
                .map(EditorContentView.class::cast)
                .filter(v->!v.hasSaved()).count();
        if (unsaved > 0) {
            showAlertDialog("关闭","继续关闭将会失去所有未保存内容，继续吗？", Alert.AlertType.CONFIRMATION)
                    .ifPresent(btn -> {
                        if (btn == ButtonType.OK) {
                            tabs.clear();
                            articleTabMap.clear();
                            this.close();
                        }
                    });
        } else {
            tabs.clear();
            articleTabMap.clear();
            this.close();
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

        Article article = entry.getKey();
        txtTitle.setText(article.getTitle());
        if (article.getType() != null) {
            txtType.setText(article.getType().getName());
        } else {
            txtType.setText("");
        }
    }

    public RectSelector getTableSelector() {
        return rectSelector;
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

    public Article getArticle(Long articleId) {
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
        EditorContentView editor = findView(EditorContentView.class);

        CodeArea codeArea = editor.getCodeArea();
        codeArea.setWrapText(true);
        codeArea.plainTextChanges().successionEnds(Duration.ofMillis(500))
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));

        ArticleContent content = article.getContent();
        if (content != null) {
            ArticleResource resource = content.getResources();
            if (resource != null) {
                Map<String,byte[]> images = resource.getImages();
                for (Map.Entry<String, byte[]> ent : images.entrySet()) {
                    editor.getImagesView().addImage(ent.getKey(), ent.getValue());
                }
                resource.getImages().clear();
            }
            String source = content.getSource();
            if(source != null) {
                codeArea.appendText(source);
            }
        }
        editor.setSaved();

        String articleSource = render.render(codeArea.getText(),editor.getImagesView().getImages());
        String renderedContext = render.renderHTML(articleSource);
        editor.getWebView().getEngine().loadContent(renderedContext);

        codeArea.textProperty().addListener(((observable, oldValue, newValue) ->{
            String source = render.render(codeArea.getText(),editor.getImagesView().getImages());
            String context = render.renderHTML(source);
            editor.getWebView().getEngine().loadContent(context);
            editor.setChanged();
            tab.setText("* " + article.getTitle());
        }));

        editor.getWebView().getEngine().getLoadWorker().stateProperty().addListener((observableValue, stateOld, stateNew) -> {
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

        BorderPane borderPane = editor.getView();
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
        this.showAlertDialog("关闭","是否要保存《" + article.getTitle() + "》?", Alert.AlertType.CONFIRMATION)
                .ifPresent(buttonType -> {
                    if (buttonType == ButtonType.OK) {
                        if (article.getType() == null) {
                            this.showAlertDialog("提示","请设置分类，然后重新保存。", Alert.AlertType.ERROR);
                            return;
                        }
                        String source = editor.getCodeArea().getText();
                        ArticleResource resource = new ArticleResource();
                        Map<String, ByteBuffer> images = editor.getImagesView().getImages();
                        Map<String, byte[]> imageData = new HashMap<>(images.size());
                        for (Map.Entry<String,ByteBuffer> item :images.entrySet()) {
                            imageData.put(item.getKey(), item.getValue().array());
                        }
                        resource.setImages(imageData);
                        ArticleContent content = article.getContent();
                        if (content == null) {
                            content = new ArticleContent();
                        }
                        content.setResources(resource);
                        content.setSource(source);
                        if(articleService.saveArticle(article, content)) {
                            tabs.remove(tab);
                            articleTabMap.remove(article);
                            this.emit(new RefreshEvent(article, this));
                        } else {
                            showAlertDialog("提示", "保存失败", Alert.AlertType.ERROR);
                        }
                    } else {
                        tabs.remove(tab);
                        articleTabMap.remove(article);
                    }
                    if (tabs.size() == 0) {
                        getStage().close();
                    }
                });
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();

        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "md-keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("CODE") != null ? "md-code":
                    matcher.group("FUNCTEX") != null ? "md-code":
                    matcher.group("LIST") != null ? "md-list":
                    matcher.group("TASK") != null ? "md-keyword":
                    matcher.group("TABLE") != null ? "md-table":
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("DESC") != null ? "string":
                    matcher.group("COMMENT") != null ? "comment":
                    matcher.group("SP") != null ? "md-sp":
                    matcher.group("TITLE") != null ? "md-keyword":
                    "text-normal";
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public static double getScreenX(Node node) {
        return  node.localToScreen(0,0).getX();
    }

    public static double getScreenY(Node node) {
        return node.localToScreen(0,0).getY();
    }


    public String reduceDesc(String text,String prefix){
        Pattern pattern = Pattern.compile(DESC_PATTERN);
        if(pattern.matcher(text).matches()){
            text = text.replaceAll("[*]","");
            text = text.replaceAll("[~]","");
        }else{
            text = prefix + text + prefix;
        }
        return text.replaceAll("\\s","");
    }

}
