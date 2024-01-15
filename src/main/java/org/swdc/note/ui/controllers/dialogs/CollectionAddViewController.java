package org.swdc.note.ui.controllers.dialogs;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.swdc.fx.FXResources;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.entities.CollectionArticle;
import org.swdc.note.core.entities.CollectionType;
import org.swdc.note.core.service.CollectionService;
import org.swdc.note.ui.view.dialogs.CollectionAddView;
import org.swdc.unitremark.UnitDocument;
import org.swdc.unitremark.UnitFullHtmlDownloadStrategies;
import org.swdc.unitremark.UnitTextDocumentGenerator;

import java.io.File;

public class CollectionAddViewController extends ViewController<CollectionAddView> {

    @FXML
    private TextField txtName;

    @Inject
    private CollectionService collectionService;

    @Inject
    private FXResources resources;

    @Inject
    private Logger logger;

    private CollectionType type;

    @FXML
    public void onAccept() {
        try {

            UnitTextDocumentGenerator cleanHtmlGenerator = new UnitTextDocumentGenerator(new UnitFullHtmlDownloadStrategies());
            UnitDocument<String> document = cleanHtmlGenerator.generateFromURL(txtName.getText());

            CollectionArticle article = new CollectionArticle();
            article.setTitle(document.getTitle());
            article.setType(this.type);
            article = collectionService.saveCollection(article);

            File assetRoot = resources.getAssetsFolder();
            File collectionsRoot = new File(assetRoot.getAbsolutePath() + File.separator + "collections");
            if (!collectionsRoot.exists()) {
                collectionsRoot.mkdirs();
            }
            File documentFile = new File(collectionsRoot.getAbsolutePath() + File.separator + article.getId());
            cleanHtmlGenerator.saveDocument(document,documentFile);

        } catch (Exception e) {
            logger.error("failed to store the document: " + txtName.getText(), e);
            Alert alert = getView().alert("失败", "存储文档失败，未知错误", Alert.AlertType.ERROR);
            alert.showAndWait();
        }

        txtName.setText(null);
        getView().hide();

    }

    @FXML
    public void onCancel() {
        txtName.setText(null);
        getView().hide();
    }

    public void setType(CollectionType type) {
        this.type = type;
    }
}
