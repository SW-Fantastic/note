package org.swdc.note.ui.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCombination;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.swdc.fx.FXView;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;

import java.util.Set;

public class UIUtils {

    public static MenuItem createMenuItem(String name, EventHandler<ActionEvent> handler, KeyCombination combination) {
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(handler);
        if (combination != null) {
            menuItem.setAccelerator(combination);
        }
        return menuItem;
    }

    public static void notification(String content, FXView view) {
        MessageView msgView = view.findComponent(MessageView.class);
        msgView.setText(content);
        Notifications.create()
                .graphic(msgView.getView())
                .position(Pos.CENTER)
                .hideAfter(Duration.seconds(2))
                .hideCloseButton()
                .show();
    }

    public static TreeItem<ArticleType> findTypeItem(TreeItem<ArticleType> typeNode, ArticleType type) {
        if (typeNode.getValue() != null && typeNode.getValue().getId().equals(type.getId())) {
            return typeNode;
        }
        if (typeNode.getChildren().size() > 0) {
            for (TreeItem<ArticleType> item: typeNode.getChildren()) {
                if (item.getValue().getId().equals(type.getId())) {
                    return item;
                } else if (item.getChildren().size() > 0){
                    return findTypeItem(item,type);
                }
            }
        }
        return null;
    }

    public static TreeItem<ArticleType> createTypeTree(ArticleType type) {
        TreeItem<ArticleType> item = new TreeItem<>(type);
        if (type.getChildren().size() > 0) {
            for (ArticleType subType: type.getChildren()) {
                TreeItem<ArticleType> subItem = createTypeTree(subType);
                item.getChildren().add(subItem);
            }
        }
        return item;
    }

    /**
     * 创建TypeTree，同时带着里面的Article
     * @param type
     * @return
     */
    public static TreeItem<Object> createTypeTreeExternal(ArticleType type) {
        TreeItem item = new TreeItem<>(type);
        Set<Article> articles = type.getArticles();
        for (Article article:articles) {
            item.getChildren().add(new TreeItem<>(article));
        }
        if (type.getChildren().size() > 0) {
            for (ArticleType subType: type.getChildren()) {
                TreeItem<ArticleType> subItem = createTypeTree(subType);
                item.getChildren().add(subItem);
            }
        }
        return item;
    }

}
