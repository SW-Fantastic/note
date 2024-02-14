package org.swdc.note.ui.controllers;

import jakarta.inject.Inject;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.PopOver;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.ShortArticle;
import org.swdc.note.core.entities.ShortArticleTag;
import org.swdc.note.core.service.ShortArticleService;
import org.swdc.note.ui.view.TopicSubView;
import org.swdc.note.ui.view.cells.ArticleShortTagCell;
import org.swdc.note.ui.view.cells.ArticleTagCell;
import org.swdc.note.ui.view.cells.ShortArticleCell;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TopicSubViewController extends ViewController<TopicSubView> {

    @Inject
    private ShortArticleService shortArticleService;

    @Inject
    private MaterialIconsService iconsService;

    @FXML
    private TextField tagField;

    @FXML
    private ListView<ShortArticleTag> tags;

    @FXML
    private ListView<ShortArticleTag> allTagList;

    @FXML
    private Button buttonSave;

    @FXML
    private TextArea txtArticle;

    @FXML
    private ListView<ShortArticle> articlesList;

    private PopOver tagsPopover;

    private ListView<ShortArticleTag> tagsView;

    private boolean showPopover;

    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {
        tagsView = new ListView<>();
        tagsPopover = new PopOver(tagsView);
        tagsPopover.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        tagsPopover.setMaxWidth(240);
        tagsPopover.setMaxHeight(120);

        tagsView.setPrefWidth(240);
        tagsView.setPrefHeight(120);
        tagsView.setOnMouseClicked(e -> {
            ShortArticleTag tag = tags.getSelectionModel().getSelectedItem();
            if (tag == null || e.getClickCount() < 2) {
                return;
            }
            appendTag(tag);
            tagsPopover.hide();
        });

        tags.setCellFactory(c -> new ArticleTagCell(iconsService,this));

        tagField.textProperty().addListener(this::onTagTextChanged);
        tagField.focusedProperty().addListener(f -> {
            if (!tagField.isFocused() && !tagField.isFocusWithin()) {
                tagsPopover.hide();
            }
        });

        buttonSave.setDisable(true);

        allTagList.setCellFactory(c -> new ArticleShortTagCell(iconsService,this));
        allTagList.getSelectionModel().selectedItemProperty().addListener(this::reloadArticles);
        reloadTags();

        articlesList.setCellFactory(v -> new ShortArticleCell(iconsService));
    }

    private void reloadArticles(Observable observable) {

        ShortArticleTag tag = allTagList.getSelectionModel().getSelectedItem();
        if (tag == null) {
            // no tag was selected
            return;
        }
        List<ShortArticle> articles = shortArticleService.getArticles(tag.getId());
        ObservableList<ShortArticle> obsArticles = articlesList.getItems();
        obsArticles.clear();
        obsArticles.addAll(articles);

    }

    @FXML
    public void createArticle() {

        if (txtArticle.getText().isBlank()) {
            Alert alert = getView().alert("提示","内容不能为空", Alert.AlertType.ERROR);
            alert.showAndWait();
            return;
        }

        ObservableList<ShortArticleTag> tagsList = tags.getItems();
        if (tagsList.isEmpty()) {
            return;
        }

        ShortArticle article = shortArticleService.createArticle(
                txtArticle.getText(),tagsList
        );

        if (article != null) {
            reloadArticles(null);
            txtArticle.clear();
            tagsList.clear();
        }
    }

    public void tagKeyReleased(KeyEvent event) {

        if (event.getCode() != KeyCode.ENTER) {
            showPopover = true;
            return;
        }

        ShortArticleTag tag = tagsView.getSelectionModel().getSelectedItem();
        if (tag == null) {
            tag = shortArticleService.createTag(tagField.getText().trim());
            reloadTags();
        }
        this.appendTag(tag);
        tagField.clear();
        onTagTextChanged(null);
        if (tagsPopover.isShowing()) {
            tagsPopover.hide();
        }

    }

    private void appendTag(ShortArticleTag tag) {
        ObservableList<ShortArticleTag> tags = this.tags.getItems();
        for (ShortArticleTag item : tags) {
            if (item.getId().equals(tag.getId())) {
                return;
            }
        }
        tags.add(tag);
        if (tags.size() >= 3) {
            tagField.setDisable(true);
        }
        buttonSave.setDisable(false);
        showPopover = false;
    }

    public void trashTag(ShortArticleTag tag) {
        if (tag == null) {
            return;
        }
        if(shortArticleService.removeTag(tag.getId())) {
            showPopover = false;
            onTagTextChanged(null);
            reloadTags();
        }
    }

    public void removeTag(ShortArticleTag tag) {
        ShortArticleTag selected = null;
        ObservableList<ShortArticleTag> tags = this.tags.getItems();
        for (ShortArticleTag item : tags) {
            if (item.getId().equals(tag.getId())) {
                selected = item;
                break;
            }
        }
        tags.remove(selected);
        if (tags.size() < 3) {
            tagField.setDisable(false);
        }
        if (tags.isEmpty()) {
            buttonSave.setDisable(true);
        }
    }

    private void onTagTextChanged(Observable observable) {

        List<ShortArticleTag> tags = shortArticleService
                .searchTagsBy(tagField.getText());

        ObservableList<ShortArticleTag> articleTags = tagsView.getItems();
        articleTags.clear();
        articleTags.addAll(tags);

        if (!tagsPopover.isShowing() && showPopover) {
            tagsPopover.show(tagField);
            showPopover = false;
        }

    }


    private void reloadTags() {

        List<ShortArticleTag> tags = shortArticleService.getTags();
        ObservableList<ShortArticleTag> articleTags = allTagList.getItems();
        articleTags.clear();
        articleTags.addAll(tags);

    }
}
