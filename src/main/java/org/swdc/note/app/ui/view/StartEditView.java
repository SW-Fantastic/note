package org.swdc.note.app.ui.view;

import com.sun.javafx.scene.input.ExtendedInputMethodRequests;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.options.DataHolder;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.Getter;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.event.ResetEvent;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.dialogs.ImageDialog;
import org.swdc.note.app.ui.view.dialogs.TableDialog;
import org.swdc.note.app.util.DataUtil;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 编辑器的界面
 * 子谦 2018 - 8 -22
 */
@FXMLView(value = "/view/editView.fxml")
public class StartEditView extends AbstractFxmlView{

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
    private static final String TITLE_PATTERN = "[#]{1,6}\\s";
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
    private static final String TABLE_PATTERN = "((\\|[\\s\\S]+\\|)([\\S]+[\\s\\S]?\\|)?)|(([\\S]+[\\S\\s]?\\|)+[\\S]+[\\S\\s]?)";
    // 匹配任务列表
    private static final String TASK_PATTERN = "[-]\\s\\[([x]?|[\\s]?)\\]";
    // 加粗，斜体，删除线
    private static final String DESC_PATTERN = "([*]{2}[\\S]+[\\s\\S]?[*]{2})|([*][\\S]+[\\s\\S]?[*]|([~]{2})[\\S]+[\\s\\S]?[~]{2})|([`][\\S]+[\\s\\S]?[`])";
    // 匹配注释
    private static final String COMMENT_PATTERN = "([<][!][-]{2}[\\s\\S]*)|([-]{2}[>])";

    private static final String FUNCTEX_PATTERN ="\\$\\$[\\s\\S]*\\$\\$";

    @Autowired
    private Parser parser;

    @Autowired
    private HtmlRenderer renderer;

    @Getter
    private Stage stage;

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
                    + "|(?<TITLE>" + TITLE_PATTERN + ")"
    );
    @Autowired
    private UIConfig config;

    @Autowired
    private ImageDialog imageDialog;

    @Autowired
    private TableDialog tableDialog;

    /**
     * 如果inputRequest为null，那么MAC系统将会出现无法输入中文的问题。
     */
    private static class InputMethodRequestsObject implements ExtendedInputMethodRequests {

        private CodeArea area;

        InputMethodRequestsObject(CodeArea area){
            this.area = area;
        }

        @Override
        public int getInsertPositionOffset() {
            return area.getCaretPosition();
        }

        @Override
        public String getCommittedText(int begin, int end) {
            return area.getText(begin,end);
        }

        @Override
        public int getCommittedTextLength() {
            return area.getText().length();
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
    protected void initUI() throws Exception{
        BorderPane pane = (BorderPane)this.getView();
        UIUtil.configTheme(pane,config);
        CodeArea codeArea = new CodeArea();
        codeArea.setInputMethodRequests(new InputMethodRequestsObject(codeArea));
        codeArea.setOnInputMethodTextChanged(e->{
            if(e.getCommitted() != null){
                codeArea.insertText(codeArea.getCaretPosition(),e.getCommitted());
            }
        });
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.plainTextChanges().successionEnds(Duration.ofMillis(500))
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));
        codeArea.getStyleClass().add("code-area");
        codeArea.setWrapText(true);
        SplitPane viewerPane = (SplitPane) getView().lookup("#viewerPane");

        BorderPane codePane = (BorderPane) findById("codeView",viewerPane.getItems());
        codePane.setCenter(codeArea);

        // javaFX SpringBoot Support库不能够在FXML中初始化webView。
        Platform.runLater(()->{
            WebView contentView = new WebView();
            BorderPane paneWeb = (BorderPane)findById("contentView",viewerPane.getItems());
            paneWeb.setCenter(contentView);
            contentView.getEngine().getLoadWorker().stateProperty().addListener(((observable, oldValue, newValue) -> {
                if(newValue.equals(Worker.State.SUCCEEDED)){
                    // 计算当前行
                    int currLine = codeArea.getText().substring(0,codeArea.getCaretPosition()).split("\n").length;
                    // 总行数
                    int lines = codeArea.getText().split("\n").length;
                    double scrollPos = 1;
                    // 计算滚动位置
                    if(lines > 0 && currLine>0){
                        scrollPos = ((double)currLine) / ((double) lines);
                    }
                    contentView.getEngine().executeScript("window.scrollTo(0, document.body.clientHeight * "+scrollPos+");");
                }
            }));
            codeArea.textProperty().addListener(((observable, oldValue, newValue) ->{
                String context = codeArea.getText();
                // 匹配双$符，在这之间的是公式
                Pattern pattern = Pattern.compile("\\$\\$[\\s\\S]*\\$\\$");
                Matcher matcher = pattern.matcher(context);
                Map<String,String> funcsMap = new HashMap<>();
                // 匹配到了一个
                while (matcher.find()){
                    // 获取内容，转换为base64
                    String result = matcher.group();
                    result = result.substring(2,result.length() - 2);
                    if (result.trim().equals("")){
                        continue;
                    }
                    String funcData = DataUtil.compileFunc(result);
                    if (funcData != null){
                        // 准备图片
                        funcsMap.put(result,funcData);
                        context = context.replace("$$"+result+"$$","![func]["+result.trim()+"]");
                    }
                }
                StringBuilder sb = new StringBuilder();
                sb.append("\r\n");
                imageDialog.getImages().entrySet().forEach(ent->
                    sb.append("[")
                            .append(ent.getKey())
                            .append("]: data:image/png;base64,")
                            .append(ent.getValue())
                            .append("\n"));
                funcsMap.entrySet().forEach(ent->
                        sb.append("[")
                                .append(ent.getKey().trim())
                                .append("]: data:image/png;base64,")
                                .append(ent.getValue())
                                .append("\n"));
                String content = renderer.render(parser.parse(context+"\n"+sb.toString()));
                contentView.getEngine().loadContent("<!doctype html><html><head><style>"+config.getMdStyleContent()+"</style></head>"
                        +"<body ondragstart='return false;'>"+content+"</body></html>");
            }));
        });

        ContextMenu menu = new ContextMenu();
        MenuItem itemCopy = new MenuItem("复制 (Ctrl/Command + C)");
        MenuItem itemPaste = new MenuItem("黏贴 (Ctrl/Command + V)");
        MenuItem itemUndo = new MenuItem("撤销 (Ctrl/Command + Z)");
        MenuItem itemRedo = new MenuItem("重做 (Ctrl/(Command + Shift) + Y/Z)");

        menu.getItems().add(itemCopy);
        menu.getItems().add(itemPaste);
        menu.getItems().add(itemUndo);
        menu.getItems().add(itemRedo);

        itemCopy.setOnAction(e->codeArea.copy());
        itemPaste.setOnAction(e->codeArea.paste());
        codeArea.undoAvailableProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && newValue){
                itemUndo.setDisable(false);
            }else {
                itemUndo.setDisable(true);
            }
        });
        itemUndo.setOnAction(e->codeArea.undo());
        codeArea.redoAvailableProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null && newValue){
                itemRedo.setDisable(false);
            }else{
                itemRedo.setDisable(true);
            }
        });
        itemRedo.setOnAction(e->codeArea.redo());

        menu.getStyleClass().add("edit-menu");

        codeArea.setContextMenu(menu);

        initEditTool();
        if(UIUtil.isClassical()){
            Platform.runLater(()->{
                String res = new StringBuilder(UIConfig.getConfigLocation()).append("res/").append(config.getBackground()).toString();
                this.getView().setStyle(pane.getStyle()+";-fx-background-image: url("+res+");");
                stage = new Stage();
                Scene scene = new Scene(this.getView());
                stage.setScene(scene);
                stage.setMinWidth(800);
                stage.setMinHeight(600);
                stage.getIcons().addAll(UIConfig.getImageIcons());
                stage.setTitle("编辑");
                imageDialog.getStage().initOwner(stage);
                tableDialog.getStage().initOwner(stage);
            });
        }
    }

    private void initEditTool(){
        SplitPane viewerPane = (SplitPane) getView().lookup("#viewerPane");
        BorderPane pane = (BorderPane)findById("codeView",viewerPane.getItems());
        ToolBar toolBar = (ToolBar) pane.getTop();
        CodeArea code = (CodeArea)pane.getCenter();
        // 加粗按钮的处理
        initButton("big",toolBar.getItems(),"bold",e->{
            String sel = code.getSelectedText();
            IndexRange range = code.getSelection();
            if(sel==null||sel.equals("")){
                // 获取光标位置
                IndexRange rgCurr = new IndexRange(code.getCaretPosition(),code.getCaretPosition());
                // 插入内容
                code.replaceText(rgCurr,"**内容写在这里**");
                return;
            }
            sel = reduceDesc(sel,"**");
            code.replaceText(range.getStart(),range.getEnd(),sel);
         });
        // 斜体按钮的处理
        initButton("italic",toolBar.getItems(),"italic",e->{
            String sel = code.getSelectedText();
            IndexRange range = code.getSelection();
            if(sel==null||sel.equals("")){
                IndexRange rgCurr = new IndexRange(code.getCaretPosition(),code.getCaretPosition());
                code.replaceText(rgCurr,"*内容写在这里*");
                return;
            }
            sel = reduceDesc(sel,"*");
            code.replaceText(range.getStart(),range.getEnd(),sel);
        });
        // 删除线的处理
        initButton("delLine",toolBar.getItems(),"strikethrough",e->{
            String sel = code.getSelectedText();
            IndexRange range = code.getSelection();
            if(sel==null||sel.equals("")){
                IndexRange rgCurr = new IndexRange(code.getCaretPosition(),code.getCaretPosition());
                code.replaceText(rgCurr,"~~内容写在这里~~");
                return;
            }
           sel = reduceDesc(sel,"~~");
            code.replaceText(range.getStart(),range.getEnd(),sel);
        });

        Optional.ofNullable(findById("title",toolBar.getItems())).ifPresent(item->{
            MenuButton btn = (MenuButton)item;
            btn.setFont(UIConfig.getFontIconSmall());
            btn.setText(String.valueOf(UIConfig.getAwesomeMap().get("header")));
            btn.getItems().forEach(this::initHeaderMenu);
        });

        Optional.ofNullable((Button)getView().lookup("#addType")).ifPresent(btn->{
            btn.setFont(UIConfig.getFontIconSmall());
            btn.setText(String.valueOf(UIConfig.getAwesomeMap().get("plus")));
        });

        Optional.ofNullable((Button)getView().lookup("#savebtn")).ifPresent(btn->{
            btn.setFont(UIConfig.getFontIconSmall());
            btn.setText(String.valueOf(UIConfig.getAwesomeMap().get("save")));
        });

        initButton("create",toolBar.getItems(),"sticky_note",e->{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("要放弃现在编辑的内容，开始新的创作吗？");
            alert.setTitle("提示");
            if(stage != null){
                alert.initOwner(stage);
            }else{
                alert.initOwner(GUIState.getStage());
            }
            alert.setHeaderText(null);
            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent(btnSel->{
                if(btnSel.equals(ButtonType.OK)) {
                    config.publishEvent(new ResetEvent(StartEditView.class));
                }
            });
        });

        initButton("tab",toolBar.getItems(),"table",e->{
            Stage stage = tableDialog.getStage();
            if (!stage.isShowing()){
                stage.showAndWait();
            }
            IndexRange rgCurr = new IndexRange(code.getCaretPosition(),code.getCaretPosition());
            code.replaceText(rgCurr,"\n\n"+tableDialog.getTable());
        });
        initButton("ol",toolBar.getItems(),"list_ol",e->{
            int idx = 1;
            String txt = code.getSelectedText();
            if(txt == null || txt.equals("")){
                IndexRange rgCurr = new IndexRange(code.getCaretPosition(),code.getCaretPosition());
                code.replaceText(rgCurr," 1. 列表项a \n 2. 列表项b \n 3. 列表项c");
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
            code.replaceText(code.getSelection(),sb.toString());
        });
        initButton("ul",toolBar.getItems(),"list_ul",e->{
            String txt = code.getSelectedText();
            if(txt == null || txt.equals("")){
                IndexRange rgCurr = new IndexRange(code.getCaretPosition(),code.getCaretPosition());
                code.replaceText(rgCurr," - 列表项a \n - 列表项b \n - 列表项c");
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
            code.replaceText(code.getSelection(),sb.toString());
        });
        initButton("img",toolBar.getItems(),"image",e->{
            Stage stg = imageDialog.getStage();
            if(!stg.isShowing()){
                stg.showAndWait();
            }
            Optional.ofNullable(imageDialog.getSelectedImage()).ifPresent(img->{
                IndexRange rgCurr = new IndexRange(code.getCaretPosition(),code.getCaretPosition());
                code.replaceText(rgCurr,"![输入简介]["+img+"]");
            });
        });
        initButton("quote",toolBar.getItems(),"quote_right",e->{
            String text = code.getSelectedText();
            if(text==null||text.equals("")){
                IndexRange rgCurr = new IndexRange(code.getCaretPosition(),code.getCaretPosition());
                code.replaceText(rgCurr,"\n> 输入引用的内容\n");
                return;
            }
            StringBuilder sb = new StringBuilder("\n");
            List<String> list = Arrays.asList(text.split("\n"));
            for (String str:  list) {
                sb.append("\n> "+str );
            }
            code.replaceText(code.getSelection(),sb.toString());
        });
        initButton("code",toolBar.getItems(),"code",e->{
            String sel = code.getSelectedText();
            IndexRange range = code.getSelection();
            if (sel == null|| sel.equals("")){
                IndexRange rgCurr = new IndexRange(code.getCaretPosition(),code.getCaretPosition());
                code.replaceText(rgCurr,"\n```language\n \\\\ 在这里编写代码 \n ```");
                return;
            }
            sel = "```language\n"+sel+"\n```";
            code.replaceText(range.getStart(),range.getEnd(),sel);
        });
    }

    public void reset(){
        BorderPane pane = (BorderPane)findById("codeView",((SplitPane)((BorderPane)getView()).getCenter()).getItems());
        CodeArea code = (CodeArea)pane.getCenter();
        code.replaceText(0,code.getText().length(),"");
    }

    private String reduceDesc(String text,String prefix){
        Pattern pattern = Pattern.compile(DESC_PATTERN);
        if(pattern.matcher(text).matches()){
            text = text.replaceAll("[*]","");
            text = text.replaceAll("[~]","");
        }else{
            text = prefix + text + prefix;
        }
        return text.replaceAll("\\s","");
    }

    private void initButton(String id, ObservableList<Node> list, String icon, EventHandler<ActionEvent> action){
        Optional.ofNullable(findById(id,list)).ifPresent(item->{
            Button btn = (Button) item;
            btn.setFont(UIConfig.getFontIconSmall());
            btn.setText(String.valueOf(UIConfig.getAwesomeMap().get(icon)));
            btn.setOnAction(action);
        });
    }

    /**
     * 处理标题选择（H1-H6）
     * @param item 菜单按钮的菜单项
     */
    private void initHeaderMenu(MenuItem item){
        int level = Integer.valueOf(item.getId().replace("h",""));
        BorderPane pane = (BorderPane)findById("codeView",((SplitPane)((BorderPane)getView()).getCenter()).getItems());
        CodeArea code = (CodeArea)pane.getCenter();
        item.setOnAction(act->{
            IndexRange range = code.getSelection();
            String sel = code.getSelectedText();
            if(sel==null||sel.equals("")){
                IndexRange rgCurr = new IndexRange(code.getCaretPosition(),code.getCaretPosition());
                for (int idx = 0 ;idx <level;idx++){
                    sel = sel + "#";
                }
                code.replaceText(rgCurr,"\n"+sel+"  这里是标题");
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
                     "text-normal"; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private Object findById(String id, ObservableList<Node> list){
        for (Node node:list) {
            if(node.getId()!=null&&node.getId().equals(id)){
                return node;
            }
        }
        return null;
    }

    public String getDocument(){
        BorderPane pane = (BorderPane)findById("codeView",((SplitPane)((BorderPane)getView()).getCenter()).getItems());
        CodeArea code = (CodeArea)pane.getCenter();
        return code.getText();
    }

    public Map<String,String> getImageRes(){
        return imageDialog.getImages();
    }

    public void setContext(String context){
        BorderPane pane = (BorderPane)findById("codeView",((SplitPane)((BorderPane)getView()).getCenter()).getItems());
        CodeArea code = (CodeArea)pane.getCenter();
        code.clear();
        code.appendText(context);
    }

}
