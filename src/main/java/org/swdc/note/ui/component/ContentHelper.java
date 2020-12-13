package org.swdc.note.ui.component;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Screen;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.fxmisc.richtext.CodeArea;
import org.swdc.fx.FXView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ContentHelper {

    private Consumer<List<KeyWord>> beforeShowTips;

    private Bounds prevLocation;

    private static class ContentTipCell extends ListCell<KeyWord> {
        @Override
        protected void updateItem(KeyWord keyWord, boolean empty) {
            super.updateItem(keyWord, empty);
            if (empty) {
                setGraphic(null);
                return;
            }
            HBox layout = new HBox();
            layout.prefWidthProperty().bind(getListView().widthProperty().subtract(32));
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(0,6,0,6));

            HBox keyWords = new HBox();
            keyWords.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(keyWords, Priority.ALWAYS);

            HBox tip = new HBox();
            tip.setAlignment(Pos.CENTER_RIGHT);
            HBox.setHgrow(tip,Priority.ALWAYS);

            layout.getChildren().addAll(keyWords,tip);

            Label lblKeyWord = new Label();
            Label lblTip = new Label();
            lblKeyWord.setText(keyWord.getKeyWord());
            lblTip.setText(keyWord.getTip());
            keyWords.getChildren().add(lblKeyWord);
            tip.getChildren().add(lblTip);
            setGraphic(layout);
        }
    }

    public static class KeyWord {
        private String keyWord;
        private String tip;
        private Function<KeyWord,String> insertAction = KeyWord::getKeyWord;

        public KeyWord(String keyWord,String tip) {
            this.keyWord = keyWord;
            this.tip = tip;
        }

        public KeyWord(String keyWord,String tip,Function<KeyWord,String> onInsert) {
            this(keyWord,tip);
            this.insertAction = onInsert;
        }

        public String getKeyWord() {
            return keyWord;
        }

        public String getTip() {
            return tip;
        }

        @Override
        public String toString() {
            return keyWord + "\t(" + tip + ") ";
        }
    }

    private List<KeyWord> keyWords = null;

    private PopOver popOver;

    public void setUpTooltip(CodeArea area, FXView view, boolean enableAutoTip,List<KeyWord> keyWords) {
        this.keyWords = keyWords;
        ListView<KeyWord> wordsList = new ListView<>();
        wordsList.setPrefHeight(240);
        wordsList.getStyleClass().add("select-list");
        wordsList.getStyleClass().add("txt-second");
        wordsList.setCellFactory((listView) -> new ContentTipCell());

        BorderPane contentPane = new BorderPane();
        contentPane.setCenter(wordsList);

        HBox bottomLayout = new HBox();
        Label tip = new Label("按下ESC以关闭，使用快捷键Alt + / 可以显示此补全菜单。");
        bottomLayout.setAlignment(Pos.CENTER);
        bottomLayout.getChildren().add(tip);
        bottomLayout.getStyleClass().add("tip-small");
        bottomLayout.setPadding(new Insets(4,4,4,4));
        contentPane.setBottom(bottomLayout);

        popOver = new PopOver();
        popOver.setContentNode(contentPane);
        popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        if (enableAutoTip) {
            area.caretBoundsProperty().addListener((observableValue, bounds, newBounds) ->  {
                Bounds bound = newBounds.isPresent() ? newBounds.get() : bounds.isPresent() ? bounds.get() : null;
                if (bound != null) {
                    prevLocation = area.screenToLocal(bound);
                    int end = area.getCaretPosition();
                    int start = end > 2 ? end - 2 : end > 1 ? end - 1 : end;
                    IndexRange range = new IndexRange(start, end);
                    String last = area.getText(range).replace(" ","");
                    List<KeyWord> content = new ArrayList<>();
                    if (this.beforeShowTips != null) {
                        this.beforeShowTips.accept(content);
                    }

                    List<KeyWord> words = content.stream()
                            .filter(item -> item.getKeyWord().startsWith(last))
                            .collect(Collectors.toList());
                    List<KeyWord> wordKeys = keyWords.stream()
                            .filter(item -> item.getKeyWord().startsWith(last))
                            .collect(Collectors.toList());

                    wordsList.getItems().clear();
                    wordsList.getItems().addAll(words);
                    wordsList.getItems().addAll(wordKeys);

                    if (!(last.isBlank() || last.isEmpty()) && words.size() + wordKeys.size() > 0) {
                        this.doShow(view,prevLocation);
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
                if (word != null &&(start == end || words.size() == 0)) {
                    area.insertText(end,word.insertAction.apply(word));
                } else if (word != null){
                    area.replaceText(range,word.insertAction.apply(word));
                }
                popOver.hide();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                popOver.hide();
            }
        });

        wordsList.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2) {
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
                if (word != null &&(start == end || words.size() == 0)) {
                    area.insertText(end,word.insertAction.apply(word));
                } else if (word != null){
                    area.replaceText(range,word.insertAction.apply(word));
                }
                popOver.hide();
            }
        });

        area.setOnKeyPressed(e -> {
            if (e.isAltDown() && e.getCode() == KeyCode.SLASH) {
                Optional<Bounds> bounds = area.getCaretBounds();
                if (bounds.isEmpty()){
                    return;
                }
                List<KeyWord> content = new ArrayList<>();
                if (this.beforeShowTips != null) {
                    this.beforeShowTips.accept(content);
                }

                if (prevLocation == null) {
                    prevLocation = area.getBoundsInLocal();
                }
                Bounds location = prevLocation;
                wordsList.getItems().clear();
                wordsList.getItems().addAll(keyWords);
                wordsList.getItems().addAll(content);
                doShow(view,location);
            }
        });
    }

    private void doShow(FXView view,Bounds location) {
        BorderPane node = view.getView();
        Bounds viewPosition = node.localToScreen(node.getBoundsInLocal());
        if (viewPosition.getMinY() + location.getCenterY() + 150 > (viewPosition.getCenterY() + node.getHeight()) / 2) {
            if (viewPosition.getMinX() + location.getCenterX() <= 150) {
                popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
            } else if (Screen.getPrimary().getBounds().getWidth()- viewPosition.getMinX() - location.getCenterX() <= 150) {
                popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_CENTER);
            } else {
                popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
            }
        } else {
            if (viewPosition.getMinX() + location.getCenterX() <= 150) {
                popOver.setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
            } else if (Screen.getPrimary().getBounds().getWidth() - viewPosition.getMinX() - location.getCenterX() <= 150) {
                popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_CENTER);
            } else {
                popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
            }
        }
        popOver.show(node,viewPosition.getMinX() + location.getCenterX()
                ,viewPosition.getMinY() + location.getCenterY());
    }

    public void beforeShow(Consumer<List<KeyWord>> beforeShow) {
        this.beforeShowTips = beforeShow;
    }

    public void cancel() {
        this.popOver.hide(Duration.ZERO);
    }

}
