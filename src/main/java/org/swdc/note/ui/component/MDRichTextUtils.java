package org.swdc.note.ui.component;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown高亮辅助类
 * 协助处理Markdown编辑器的文本高亮效果。
 */
public class MDRichTextUtils {


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
    public static StyleSpans<Collection<String>> computeHighlighting(String text) {
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
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public static String reduceDesc(String text,String prefix){
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
