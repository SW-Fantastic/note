package org.swdc.note.ui.view;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.render.HTMLRender;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.component.TypeListPopover;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@View(title = "阅读",css = "editor.css",viewLocation = "views/main/ReaderView.fxml")
public class ReaderView extends AbstractView {

    private ObservableList<Tab> articles = FXCollections.observableArrayList();
    private Map<Article, Tab> articleTabMap = new HashMap<>();

    private TypeListPopover popover;

    @Inject
    private HTMLRender render = null;

    @Inject
    private MaterialIconsService iconsService = null;

    @Inject
    private ArticleService articleService = null;

    @PostConstruct
    public void initialize() {
        this.popover = new TypeListPopover();
        this.popover.onClick(article -> {
            Article existed = getArticle(article.getId());
            if (existed != null) {
                Tab tab = articleTabMap.get(existed);
                TabPane articlesTab = findById("articleTab");
                articlesTab.getSelectionModel().select(tab);
            } else {
                this.addArticle(article);
            }
        });
        TabPane articlesTab = findById("articleTab");
        Bindings.bindContentBidirectional(articlesTab.getTabs(), articles);
        Stage stage = getStage();
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        this.initViewToolButton("edit","edit");
        this.initViewToolButton("delete","delete");
        this.initViewToolButton("toc","library_books");
        stage.setOnCloseRequest(e -> {
            if (popover.isShowing()) {
                popover.hide(Duration.ZERO);
            }
            articles.clear();
            articleTabMap.clear();
        });
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

    public Article getArticle(String path) {
        if (path == null || path.isEmpty() || path.isBlank()) {
            return null;
        }
        Article select = articleTabMap
                .keySet()
                .stream()
                .filter(k -> k.getSingleStore() != null)
                .filter(k -> k.getFullPath().equals(path))
                .findFirst()
                .orElse(null);
        return select;
    }

    public void refresh(String articleId) {
        Article article = getArticle(articleId);
        if (article == null) {
            return;
        }
        Tab tab = articleTabMap.get(article);
        Article refreshed = articleService.getArticle(articleId);
        ArticleContent content = articleService.getContentOf(refreshed);
        article.setContent(content);
        WebView view = (WebView) tab.getContent();

        String articleSource = render.renderBytes(content.getSource(),content.getImages());
        String renderedContext = render.renderHTML(articleSource);
        view.getEngine().loadContent(renderedContext);
    }

    public void refreshByExternal(Article refreshed) {
        Article old = getArticle(refreshed.getFullPath());
        old.setContent(refreshed.getContent());

        Tab tab = articleTabMap.get(old);
        old.setTitle(refreshed.getTitle());
        old.setDesc(refreshed.getDesc());

        WebView view = (WebView) tab.getContent();
        ArticleContent content = refreshed.getContent();

        String articleSource = render.renderBytes(content.getSource(),content.getImages());
        String renderedContext = render.renderHTML(articleSource);
        view.getEngine().loadContent(renderedContext);
    }

   /* public void refreshByPath(String path) {
        Article article = getArticle(path);
        if (article == null) {
            return;
        }*/
        //Tab tab = articleTabMap.get(article);
        //ContentFormatter formatter = (ContentFormatter) findComponent(article.getContentFormatter());
        //Article refreshed = (Article) formatter.load(Paths.get(path));
        /*article.setContent(refreshed.getContent());
        article.setTitle(refreshed.getTitle());
        article.setDesc(refreshed.getDesc());
        WebView view = (WebView) tab.getContent();
        ArticleContent content = refreshed.getContent();

        String articleSource = render.renderBytes(content.getSource(),content.getImages());
        String renderedContext = render.renderHTML(articleSource);
        view.getEngine().loadContent(renderedContext);*/
   // }

    public Tab addArticle(Article article) {
        Article hasOpen = null;
        if (article.getId() != null) {
            hasOpen = getArticle(article.getId());
        }

        TabPane tabPane = findById("articleTab");
        if(hasOpen != null) {
            Tab tab =  articleTabMap.get(hasOpen);
            tabPane.getSelectionModel().select(tab);
            return tab;
        }
        Tab tab = new Tab();
        tab.setClosable(true);
        tab.setText(article.getTitle());
        tab.setClosable(true);
        WebView view = new WebView();

        ArticleContent content = Optional
                .ofNullable(article.getContent())
                .orElse(articleService.getContentOf(article));

        String articleSource = render.renderBytes(content.getSource(),content.getImages());
        String renderedContext = render.renderHTML(articleSource);
        view.getEngine().loadContent(renderedContext);

        tab.setContent(view);
        articleTabMap.put(article, tab);
        articles.add(tab);
        view.prefHeightProperty().bind(tab.getTabPane().heightProperty().subtract(42));
        view.prefWidthProperty().bind(tab.getTabPane().widthProperty().subtract(42));
        tab.setOnCloseRequest(e -> onTabClose(e,tab,article));

        tabPane.getSelectionModel().select(tab);
        return tab;
    }

    public void closeTab(String articleId) {
        Article article = getArticle(articleId);
        Tab tab = articleTabMap.get(article);
        articles.remove(tab);
        articleTabMap.remove(article);
        if (articles.size() <= 0) {
            this.getStage().close();
        }
    }

    public Article getReadingArticle() {
        TabPane tabPane = findById("articleTab");
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

    private void onTabClose(Event e, Tab tab, Article article) {
        articles.remove(tab);
        articleTabMap.remove(article);
        if (articles.size() == 0) {
            getStage().close();
        }
        return;
    }

    public TypeListPopover getPopover() {
        return popover;
    }
}
