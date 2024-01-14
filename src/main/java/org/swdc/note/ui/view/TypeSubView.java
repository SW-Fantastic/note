package org.swdc.note.ui.view;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import org.controlsfx.control.PopOver;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.ui.controllers.TypeSubViewController;

import static org.swdc.note.ui.view.UIUtils.createMenuItem;

@View(viewLocation = "views/main/TypeSubView.fxml")
public class TypeSubView extends AbstractView {

    @Inject
    private MaterialIconsService iconsService = null;

    private ContextMenu typeContextMenu;

    private ContextMenu articleContextMenu;


    @PostConstruct
    public void initialize() {
        this.initViewToolButton("add", "add");
        this.initViewToolButton("imp","folder");
        this.initViewToolButton("help", "live_help");
        this.initTypeControlMenu();
        this.initArticleContextMenu();
    }

    private void initTypeControlMenu() {
        SimpleBooleanProperty typeNotSelectProp = new SimpleBooleanProperty(true);
        this.typeContextMenu = new ContextMenu();
        TypeSubViewController controller = getController();
        MenuItem itemAdd = createMenuItem("创建文档", controller::onCreateDocument,null);
        MenuItem itemCreate = createMenuItem("添加分类",controller::creatType, null);
        MenuItem itemDelete = createMenuItem("删除", controller::deleteType, null);
        MenuItem itemRename = createMenuItem("修改分类", controller::onModifyType,null);
        MenuItem itemExport = createMenuItem("导出", controller::exportType,null);

        itemDelete.disableProperty().bind(typeNotSelectProp);
        itemRename.disableProperty().bind(typeNotSelectProp);
        itemExport.disableProperty().bind(typeNotSelectProp);
        itemAdd.disableProperty().bind(typeNotSelectProp);

        typeContextMenu.getItems().addAll(itemAdd,new SeparatorMenuItem(),
                itemCreate, itemDelete,itemRename, itemExport);

        TreeView<ArticleType> typeTree = findById("typeTree");
        typeTree.getSelectionModel().selectedItemProperty().addListener((ob, item, newItem) -> {
            if (newItem == null) {
                typeNotSelectProp.set(true);
            } else {
                typeNotSelectProp.set(false);
            }
        });
        typeTree.setContextMenu(typeContextMenu);

    }

    private void initArticleContextMenu() {
        TypeSubViewController controller = getController();
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
