package org.swdc.note.ui.controllers;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.swdc.fx.FXResources;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.CollectionArticle;
import org.swdc.note.ui.view.CollectionReadView;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CollectionReadViewController extends ViewController<CollectionReadView> {

    @FXML
    private TabPane articlesTab;

    @Inject
    private FXResources resources;

    @Inject
    private Logger logger;

    private Map<CollectionArticle, Tab> loaded = new ConcurrentHashMap<>();

    public void addArticle(CollectionArticle article) {

        if (article.getId() == null || article.getId().isBlank()) {
            return;
        }

        for (CollectionArticle item :loaded.keySet()) {
            if (item.getId().equals(article.getId())) {
                articlesTab.getSelectionModel().select(
                        loaded.get(item)
                );
                return;
            }
        }

        File assetRoot = resources.getAssetsFolder();
        File collectionsRoot = new File(assetRoot.getAbsolutePath() + File.separator + "collections");
        File target = new File(collectionsRoot + File.separator + article.getId());

        if (!target.exists()) {
            return;
        }

        try {
            String content = Files.readString(target.toPath(), StandardCharsets.UTF_8);

            WebView webView = new WebView();
            Tab tab = new Tab();
            tab.setText(article.getTitle());
            tab.setContent(webView);
            tab.setClosable(true);
            tab.setOnClosed(e -> {
                articlesTab.getTabs().remove(tab);
                loaded.remove(article);
                if (articlesTab.getTabs().isEmpty()) {
                    getView().hide();
                }
            });
            webView.getEngine().loadContent(content);
            articlesTab.getTabs().add(tab);
            articlesTab.getSelectionModel().select(tab);

            loaded.put(article,tab);
        } catch (Exception e) {
            logger.error("failed to open file", e);
            Alert alert = this.getView().alert("错误", "无法打开指定文档《" + article.getTitle()+ "》", Alert.AlertType.ERROR);
            alert.showAndWait();
        }
    }


}
