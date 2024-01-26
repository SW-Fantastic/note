package org.swdc.note.ui.controllers.dialogs;

import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.slf4j.Logger;
import org.swdc.fx.FXResources;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.CollectionArticle;
import org.swdc.note.core.entities.CollectionFocus;
import org.swdc.note.core.entities.CollectionType;
import org.swdc.note.core.service.CollectionService;
import org.swdc.note.ui.view.UIUtils;
import org.swdc.note.ui.view.dialogs.CollectionAddView;
import org.swdc.unitremark.UnitDocument;
import org.swdc.unitremark.UnitTextDocumentGenerator;
import org.swdc.unitremark.strategies.UnitMarkdownEmbeddedStrategies;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

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

    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {


    }

    @FXML
    public void onAccept() {

        try {
            URL url = new URI(txtName.getText()).toURL();
            Thread.ofVirtual().start(() -> {


                try {

                    if (this.type == null) {
                        type = collectionService.getTypeByHost(url.getHost());
                        if (type == null) {
                            type = new CollectionType();
                            type.setTitle(url.getHost());
                            type = collectionService.saveType(type);
                        }
                    }

                    String urlEx = url.toExternalForm();
                    CollectionFocus focus = collectionService.findFocus(urlEx);

                    Document jsoupDoc = Jsoup.connect(urlEx).get();
                    String title = jsoupDoc.title();

                    UnitTextDocumentGenerator generator = new UnitTextDocumentGenerator(new UnitMarkdownEmbeddedStrategies());
                    UnitDocument<String> doc = null;
                    if (focus == null) {
                        doc = generator.generateFromSource(urlEx,jsoupDoc.toString());
                    } else {
                        Elements elements = jsoupDoc.select(focus.getSelector());
                        if (!elements.isEmpty()) {
                            Document root = Jsoup.parse("<div></div>");
                            for (Element element: elements) {
                                root.appendChild(element);
                            }
                            doc = generator.generateFromSource(urlEx,root.toString());
                        } else {
                            doc = generator.generateFromSource(urlEx,jsoupDoc.toString());
                        }
                    }

                    CollectionArticle article = new CollectionArticle();
                    article.setType(this.type);
                    article.setSource(url.toExternalForm());
                    article.setTitle(title);
                    article = collectionService.saveCollection(article);

                    File assetRoot = resources.getAssetsFolder();
                    File collectionsRoot = new File(assetRoot.getAbsolutePath() + File.separator + "collections");
                    if (!collectionsRoot.exists()) {
                        collectionsRoot.mkdirs();
                    }

                    File documentFile = new File(collectionsRoot.getAbsolutePath() + File.separator + article.getId());
                    FileOutputStream fos = new FileOutputStream(documentFile);

                    fos.write(doc.getSource().getBytes(StandardCharsets.UTF_8));
                    fos.close();
                    Platform.runLater(() -> {
                        UIUtils.notification("文档《" + title + "》");
                    });

                } catch (Exception e) {
                    logger.error("failed to download the page", e);
                    Platform.runLater(() -> {
                        Alert alert = getView().alert("失败", "存储文档失败，未知错误", Alert.AlertType.ERROR);
                        alert.showAndWait();
                    });
                }
            });

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
