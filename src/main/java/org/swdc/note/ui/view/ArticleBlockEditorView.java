package org.swdc.note.ui.view;

import jakarta.annotation.PostConstruct;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.entities.Article;

import java.util.HashMap;
import java.util.Map;

import static org.swdc.note.ui.view.UIUtils.fxViewByView;

@View(title = "编辑",viewLocation = "views/main/ArticleBlockEditorView.fxml",css = {
        "editor.css", "keywords.css"
})
public class ArticleBlockEditorView extends AbstractView {

    private Map<Article, Tab> articleTabMap = new HashMap<>();

    private ObservableList<Tab> tabs = FXCollections.observableArrayList();

    @PostConstruct
    public void init() {

        Stage stage = this.getStage();
        stage.setMinWidth(880);
        stage.setMinHeight(628);

        stage.setWidth(stage.getMinWidth());
        stage.setHeight(stage.getMinHeight());

        TabPane tabPane = findById("editorTab");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        Bindings.bindContentBidirectional(tabPane.getTabs(),tabs);


    }

    private void onTabClose(Event e, Tab tab, Article article) {
        e.consume();
        EditorBlockedContentView editor = fxViewByView(tab.getContent(), EditorBlockedContentView.class);
        String source = editor.getSource();
        editor.setSource(source);
        //tabs.remove(tab);
        //articleTabMap.remove(article);
        if (tabs.size() == 0) {
            getStage().close();
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

        EditorBlockedContentView editor = getView(EditorBlockedContentView.class);

        BorderPane borderPane = (BorderPane) editor.getView();
        borderPane.setUserData(editor);
        borderPane.prefHeightProperty().bind(tabPane.heightProperty().subtract(42));
        borderPane.prefWidthProperty().bind(tabPane.widthProperty());
        tab.setContent(borderPane);
        tab.setOnCloseRequest(e -> this.onTabClose(e, tab, article));

        articleTabMap.put(article, tab);
        tabs.add(tab);

        return tab;
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

}
