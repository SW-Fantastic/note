package org.swdc.note.app.ui.view.dialogs;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.service.TypeService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

@FXMLView("/view/articleSetDialog.fxml")
public class ArticleSetDialog extends AbstractFxmlView {

    @Autowired
    private UIConfig config;

    @Autowired
    private TypeService typeService;

    @Autowired
    private BrowserContext context;

    private TreeItem<ArticleType> root = new TreeItem<>();

    private ObservableList<Article> articles = FXCollections.observableArrayList();

    private BrowserView browserView;

    @Getter
    private Stage stage;

    @PostConstruct
    public void initUI() throws Exception {
        BorderPane pane = (BorderPane)getView();
        String res = new StringBuilder(UIConfig.getConfigLocation()).append("res/").append(config.getBackground()).toString();
        pane.setStyle(pane.getStyle()+";-fx-background-image: url("+res+");");
        SplitPane splitPane = (SplitPane) pane.getCenter();
        TreeView<ArticleType> typeTreeView = (TreeView)UIUtil.findById("typeTree",splitPane.getItems());
        typeTreeView.setRoot(root);
        root.getChildren().clear();

        typeTreeView.setCellFactory(tree -> config.getComponent(ArticleSetTreeCell.class));
        typeTreeView.setShowRoot(false);

        ListView<Article> listView = (ListView)UIUtil.findById("articleList", splitPane.getItems());
        listView.setItems(articles);

        BorderPane content = (BorderPane)UIUtil.findById("content", splitPane.getItems());
        browserView = new BrowserView(new Browser(context));
        content.setCenter(browserView);

        ToolBar toolBar = (ToolBar) content.getTop();
        Button singleImport = (Button)UIUtil.findById("btnImport", toolBar.getItems());
        singleImport.setFont(UIConfig.getFontIconSmall());
        singleImport.setText(UIConfig.getAwesomeMap().get("download") + "");

        Platform.runLater(() -> {
            try {
                stage = new Stage();
                Scene scene = new Scene(pane);
                scene.getStylesheets().add(new File("./configs/theme/" + config.getTheme() + "/stage.css").toURI().toURL().toExternalForm());
                stage.setTitle("NoteSet 记录集");
                stage.setScene(scene);
                stage.setResizable(true);
                stage.getIcons().addAll(UIConfig.getImageIcons());
                stage.setOnCloseRequest(this::reset);
                stage.setWidth(1200);
                stage.setHeight(650);
            } catch (Exception ex) {
            }
        });

        UIUtil.configTheme(pane, config);
    }

    private void reset(WindowEvent windowEvent) {
        this.reset();
    }

    public void reset() {
        root.getChildren().clear();
        articles.clear();
        browserView.getBrowser().loadHTML("<html></html>");
    }

    public void show() {
        if (this.stage.isShowing()) {
            stage.requestFocus();
        } else {
            stage.show();
        }
    }

    public void hide() {
        if (stage.isShowing()) {
            stage.hide();
        }
    }

    public void setType(ArticleType type) {
        root.getChildren().clear();
        root.getChildren().add(typeService.getExternalTypes(type));
    }

    public void setArticles(List<Article> articles) {
        this.articles.clear();
        this.articles.addAll(articles);
    }

    public void setContent(String content) {
        browserView.getBrowser().loadHTML(content);
    }

}
