package org.swdc.note.ui.view;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.service.ContentService;
import org.swdc.note.ui.component.blocks.ArticleBlock;
import org.swdc.note.ui.component.blocks.BlockData;
import org.swdc.note.ui.component.blocks.ImageBlock;
import org.swdc.note.ui.controllers.ArticleBlockEditorController;

import java.util.*;
import java.util.stream.Collectors;

import static org.swdc.note.ui.view.UIUtils.fxViewByView;

@View(title = "编辑",viewLocation = "views/main/ArticleBlockEditorView.fxml",css = {
        "editor.css", "keywords.css"
})
public class ArticleBlockEditorView extends AbstractView {

    @Inject
    private Logger logger;

    @Inject
    private MaterialIconsService iconsService;

    @Inject
    private ContentService contentService  = null;

    private Map<Article, Tab> articleTabMap = new HashMap<>();

    private ObservableList<Tab> tabs = FXCollections.observableArrayList();

    @PostConstruct
    public void init() {

        Stage stage = this.getStage();
        stage.setMinWidth(880);
        stage.setMinHeight(628);

        stage.setWidth(stage.getMinWidth());
        stage.setHeight(stage.getMinHeight());
        stage.setOnCloseRequest(this::closeRequest);

        TabPane tabPane = findById("editorTab");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        Bindings.bindContentBidirectional(tabPane.getTabs(),tabs);
        tabPane.getSelectionModel().selectedItemProperty().addListener(this::onTabChange);

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
            EditorBlockedContentView contentView = fxViewByView(select.getContent(), EditorBlockedContentView.class);
            selectEntry.getKey().setTitle(newStr);
            select.setText((contentView.isChanged() ? "" : "* ") + newStr);
        });

        Button save = findById("save");
        save.setFont(iconsService.getFont(FontSize.SMALL));
        save.setPadding(new Insets(4));
        save.setText(iconsService.getFontIcon("save"));
    }

    private void onTabClose(Event e, Tab tab, Article article) {
        e.consume();
        EditorBlockedContentView editor = fxViewByView(tab.getContent(), EditorBlockedContentView.class);
        if (!editor.isChanged()) {
            tabs.remove(tab);
            articleTabMap.remove(article);
            if (tabs.isEmpty()) {
                getStage().close();
            }
            return;
        }
        this.alert("关闭","是否要保存《" + article.getTitle() + "》?", Alert.AlertType.CONFIRMATION)
                .showAndWait()
                .ifPresent(buttonType -> {
                    if (buttonType == ButtonType.OK) {
                        ArticleBlockEditorController controller = getController();
                        controller.saveArticle(tab,article);
                    }
                    tabs.remove(tab);
                    articleTabMap.remove(article);
                    if (tabs.isEmpty()) {
                        getStage().close();
                    }
                });
    }

    private void closeRequest(Event event) {
        event.consume();
        List<EditorBlockedContentView> editors = tabs.stream()
                .map(tab->tab.getContent().getUserData())
                .map(EditorBlockedContentView.class::cast)
                .collect(Collectors.toList());
        long unsaved = editors.stream().filter(v->!v.isChanged()).count();
        if (unsaved > 0) {
            alert("关闭","继续关闭将会失去所有未保存内容，继续吗？", Alert.AlertType.CONFIRMATION)
                    .showAndWait().ifPresent(btn -> {
                        if (btn == ButtonType.OK) {
                            tabs.clear();
                            articleTabMap.clear();
                            this.hide();
                        }
                    });
        } else {
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

        ArticleContent content  = Optional.ofNullable(article.getContent())
                .orElse(contentService.getArticleContent(article.getId()));
        List<BlockData> contentData = new ArrayList<>();
        if (content != null && content.getSource() != null) {

            try {

                Map<String,byte[]> images = content.getImages();
                ObjectMapper mapper = new ObjectMapper();
                JavaType type = mapper.getTypeFactory().constructParametricType(List.class, BlockData.class);
                List<BlockData> blocks = mapper.readValue(content.getSource(),type);
                for (BlockData block : blocks) {
                    if (ImageBlock.class.getName().equals(block.getType())) {
                        try {
                            Map<String,String> header = (Map<String, String>) block.getContent();
                            byte[] data = images.get(header.get("name"));
                            String binary = Base64.getEncoder().encodeToString(data);
                            block.setSource(binary);
                            contentData.add(block);
                        } catch (Exception e) {
                        }
                    } else {
                        contentData.add(block);
                    }
                }
            } catch (Exception e) {
                logger.error("failed to load content data", e);
            }

        }

        EditorBlockedContentView editor = getView(EditorBlockedContentView.class);
        if (!contentData.isEmpty()) {
            editor.setSource(contentData);
        }
        editor.changedProperty().addListener(e -> {
            if (editor.isChanged()) {
                tab.setText(article.getTitle() + " * ");
            } else {
                tab.setText(article.getTitle());
            }
        });

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

    public void refresh() {
        Article article = getEditingArticle();
        TextField txtTitle = findById("title");
        TextField txtType = findById("type");
        txtTitle.setText(article.getTitle());
        txtType.setText(article.getType().getName());
    }

}
