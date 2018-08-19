package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.swdc.note.app.ui.UIConfig;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lenovo on 2018/8/19.
 */
@FXMLView(value = "/view/editView.fxml",css = "/style/java-keywords.css")
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
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<TITLE>" + TITLE_PATTERN + ")"
    );
    @Autowired
    private UIConfig config;

    @PostConstruct
    protected void initUI() throws Exception{
        BorderPane pane = (BorderPane)this.getView();
        CodeArea codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.plainTextChanges().successionEnds(Duration.ofMillis(500))
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));
        codeArea.getStyleClass().add("code-area");
        pane.setCenter(codeArea);

        if(config.getTheme().equals("")||config.getTheme().equals("def")){
            pane.getStylesheets().add(new ClassPathResource("style/start.css").getURL().toExternalForm());
        }else{
            pane.getStylesheets().add("file:configs/theme/"+config.getTheme()+"/"+config.getTheme()+".css");
        }
        initEditTool();
    }

    protected void initEditTool(){
        ToolBar toolBar = (ToolBar) getView().lookup("#codeTool");
        CodeArea code = (CodeArea)((BorderPane) getView()).getCenter();
        // 加粗按钮的处理
        initButton("big",toolBar.getItems(),"bold",e->{
            String sel = code.getSelectedText();
            IndexRange range = code.getSelection();
            if(sel==null||sel.equals("")){
                code.appendText("**内容写在这里**");
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
                code.appendText("*内容写在这里*");
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
                code.appendText("~~内容写在这里~~");
                return;
            }
           sel = reduceDesc(sel,"~~");
            code.replaceText(range.getStart(),range.getEnd(),sel);
        });

        Optional.ofNullable(findById("title",toolBar.getItems())).ifPresent(item->{
            MenuButton btn = (MenuButton)item;
            btn.setFont(UIConfig.getFontIconSmall());
            btn.setText(String.valueOf(UIConfig.getGLYPH_MAP().get("header")));
            btn.getItems().forEach(this::initHeaderMenu);
        });

        initButton("tab",toolBar.getItems(),"table",e->{

        });
        initButton("ol",toolBar.getItems(),"list_ol",e->{

        });
        initButton("ul",toolBar.getItems(),"list_ul",e->{

        });
        initButton("img",toolBar.getItems(),"image",e->{

        });
        initButton("task",toolBar.getItems(),"list_alt",e->{

        });
        initButton("code",toolBar.getItems(),"code",e->{

        });
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
            btn.setText(String.valueOf(UIConfig.getGLYPH_MAP().get(icon)));
            btn.setOnAction(action);
        });
    }

    /**
     * 处理标题选择（H1-H6）
     * @param item
     */
    private void initHeaderMenu(MenuItem item){
        int level = Integer.valueOf(item.getId().replace("h",""));
        CodeArea code = (CodeArea)((BorderPane) getView()).getCenter();
        item.setOnAction(act->{
            IndexRange range = code.getSelection();
            String sel = code.getSelectedText();
            if(sel==null||sel.equals("")){
                for (int idx = 0 ;idx <level;idx++){
                    sel = sel + "#";
                }
                code.appendText(sel + " 这里是标题");
                return;
            }
            if(sel.contains("#")){
                sel = sel.replaceAll("[#]","");
                code.replaceText(range.getStart(),range.getEnd(),sel);
                return;
            }
            sel = " "+sel;
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
                    matcher.group("LIST") != null ? "md-list":
                    matcher.group("TASK") != null ? "md-keyword":
                    matcher.group("TABLE") != null ? "md-table":
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("DESC") != null ? "string":
                    matcher.group("COMMENT") != null ? "comment":
                    matcher.group("SP") != null ? "md-sp":
                    matcher.group("TITLE") != null ? "md-keyword":
                     "text"; /* never happens */ assert styleClass != null;
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

}
