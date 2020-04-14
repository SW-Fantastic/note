package org.swdc.note.ui.view;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.MaterialIconsService;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.ui.controllers.TypeSubViewController;

import static org.swdc.note.ui.view.UIUtils.createMenuItem;

@View(stage = false)
public class TypeSubView extends FXView {

    @Aware
    private MaterialIconsService iconsService = null;

    private ContextMenu typeContextMenu;

    private ContextMenu articleContextMenu;

    @Override
    public void initialize() {
        Button btnSearch = findById("search");
        btnSearch.setFont(iconsService.getFont(FontSize.MIDDLE));
        btnSearch.setText(iconsService.getFontIcon("search"));
        btnSearch.setPadding(new Insets(4,4,4,4));

        this.initViewToolButton("add", "add");
        this.initViewToolButton("imp","folder");
        this.initViewToolButton("help", "live_help");
        this.initTypeControlMenu();
        this.initArticleContextMenu();
    }

    private void initTypeControlMenu() {
        SimpleBooleanProperty typeNotSelectProp = new SimpleBooleanProperty(true);
        this.typeContextMenu = new ContextMenu();
        TypeSubViewController controller = getLoader().getController();
        MenuItem itemCreate = createMenuItem("添加分类",controller::creatType, null);
        MenuItem itemDelete = createMenuItem("删除", controller::deleteType, null);
        MenuItem itemRename = createMenuItem("修改分类", controller::onModifyType,null);
        MenuItem itemExport = createMenuItem("导出", controller::exportType,null);

        itemDelete.disableProperty().bind(typeNotSelectProp);
        itemRename.disableProperty().bind(typeNotSelectProp);
        itemExport.disableProperty().bind(typeNotSelectProp);

        typeContextMenu.getItems().addAll(itemCreate, itemDelete,itemRename, itemExport);
        ListView<ArticleType> typeList = findById("typeList");
        typeList.getSelectionModel().selectedItemProperty().addListener((ob, item, newItem) -> {
            if (newItem == null) {
                typeNotSelectProp.set(true);
            } else {
                typeNotSelectProp.set(false);
            }
        });
        typeList.setContextMenu(typeContextMenu);
    }

    private void initArticleContextMenu() {
        TypeSubViewController controller = getLoader().getController();
        SimpleBooleanProperty articleNotSelectProp = new SimpleBooleanProperty(true);

        this.articleContextMenu = new ContextMenu();
        MenuItem itemView = createMenuItem("打开", controller::openArticle, null);
        MenuItem itemDelete = createMenuItem("删除", controller::deleteArticle, null);
        MenuItem itemEdit = createMenuItem("编辑", controller::editArticle, null);
        MenuItem itemExport = createMenuItem("另存为", controller::exportArticle,null);
        MenuItem itemCreate = createMenuItem("添加文档", controller::createDocument,null);

        itemView.disableProperty().bind(articleNotSelectProp);
        itemDelete.disableProperty().bind(articleNotSelectProp);
        itemEdit.disableProperty().bind(articleNotSelectProp);
        itemExport.disableProperty().bind(articleNotSelectProp);

        ListView<Article> articleList = findById("articleList");
        articleContextMenu.getItems().addAll(itemView,itemEdit , itemDelete, itemExport, new SeparatorMenuItem(),itemCreate);
        articleList.getSelectionModel().selectedItemProperty().addListener((ob, item, newItem) -> {
            if (newItem == null) {
                articleNotSelectProp.set(true);
            } else {
                articleNotSelectProp.set(false);
            }
        });
        articleList.setContextMenu(articleContextMenu);

    }

    private void initViewToolButton(String id,String icon) {
        Button btn = findById(id);
        if (btn == null) {
            return;
        }
        btn.setPadding(new Insets(4,4,4,4));
        btn.setFont(iconsService.getFont(FontSize.MIDDLE));
        btn.setText(iconsService.getFontIcon(icon));
    }

}
