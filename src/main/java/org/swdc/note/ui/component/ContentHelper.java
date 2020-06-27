package org.swdc.note.ui.component;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import lombok.AllArgsConstructor;
import org.controlsfx.control.PopOver;
import org.fxmisc.richtext.CodeArea;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ContentHelper {

    @AllArgsConstructor
    private static class KeyWord {
        private String keyWord;
        private String tip;

        public String getKeyWord() {
            return keyWord;
        }

        public String getTip() {
            return tip;
        }

        @Override
        public String toString() {
            return keyWord + " [ " + tip + " ] ";
        }
    }

    private List<KeyWord> keyWords = Arrays.asList(
            new KeyWord("*", "无序列表"),
            new KeyWord("**", "加粗"),
            new KeyWord(">", "引用"),
            new KeyWord("#","标题1"),
            new KeyWord("##","标题2"),
            new KeyWord("###","标题3"),
            new KeyWord("####","标题4"),
            new KeyWord("#####","标题5"),
            new KeyWord("######","标题6"),
            new KeyWord("$","公式"),
            new KeyWord("- [ ] ", "待办列表"),
            new KeyWord("- [x] ","待办列表"),
            new KeyWord("* * * ", "分割线"),
            new KeyWord("- - - ", "分割线")
    );

    private PopOver popOver;

    public void setUpTooltip(CodeArea area, Node view, boolean enableAutoTip) {
        ListView<KeyWord> wordsList = new ListView<>();
        wordsList.setPrefHeight(240);
        wordsList.getStyleClass().add("select-list");
        wordsList.getStyleClass().add("txt-second");

        popOver = new PopOver();
        popOver.setContentNode(wordsList);
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        if (enableAutoTip) {
            area.caretBoundsProperty().addListener((observableValue, bounds, newBounds) ->  {
                Bounds bound = newBounds.isPresent() ? newBounds.get() : bounds.isPresent() ? bounds.get() : null;
                if (bound != null) {
                    int end = area.getCaretPosition();
                    int start = end > 2 ? end - 2 : end > 1 ? end - 1 : end;
                    IndexRange range = new IndexRange(start, end);
                    String last = area.getText(range).replace(" ","");
                    List<KeyWord> words = keyWords
                            .stream()
                            .filter(item ->item.getKeyWord().startsWith(last))
                            .collect(Collectors.toList());
                    if (!(last.isBlank() || last.isEmpty()) && words.size() > 0) {
                        wordsList.getItems().clear();
                        wordsList.getItems().addAll(words);
                        popOver.show(view, bound.getCenterX(), bound.getCenterY());
                        wordsList.requestFocus();
                    } else {
                        if (popOver.isShowing()) {
                            popOver.hide();
                        }
                    }
                }
            });
        }

        wordsList.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                KeyWord word = wordsList.getSelectionModel().getSelectedItem();
                int end = area.getCaretPosition();
                int start = end > 2 ? end - 2 : end > 1 ? end - 1 : end;
                IndexRange range = new IndexRange(start,end);
                String text = area.getText(range);
                if (text.contains("\n") || text.contains("\r")) {
                    int idx = text.lastIndexOf("\r");
                    if (idx == -1) {
                        idx = text.lastIndexOf("\n");
                        start = start + idx + 1;
                    }
                }
                List<KeyWord> words = keyWords
                        .stream()
                        .filter(item ->item.getKeyWord().startsWith(text))
                        .collect(Collectors.toList());
                range = new IndexRange(start,end);
                if (start == end || words.size() == 0) {
                    area.insertText(end,word.getKeyWord());
                } else {
                    area.replaceText(range,word.getKeyWord());
                }
                popOver.hide();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                popOver.hide();
            }
        });

        area.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.CONTROL) {
                Optional<Bounds> bounds = area.getCaretBounds();
                if (bounds.isEmpty()){
                    return;
                }
                Bounds location = bounds.get();
                wordsList.getItems().clear();
                wordsList.getItems().addAll(keyWords);
                popOver.show(view,location.getCenterX(),location.getCenterY());
            }
        });

    }
}
