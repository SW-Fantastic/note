package org.swdc.note.ui.controllers.dialogs;

import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.files.SingleStorage;
import org.swdc.note.core.files.StorageFactory;
import org.swdc.note.core.proto.URLProtoResolver;
import org.swdc.note.core.service.ArticleService;
import org.swdc.note.ui.view.ArticleSetView;
import org.swdc.note.ui.view.ReaderView;
import org.swdc.note.ui.view.dialogs.SourceDialogView;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class SourceDialogController extends ViewController<SourceDialogView> {

    @FXML
    private TextField txtURI;

    @Inject
    private ArticleService service = null;

    @Inject
    private Logger logger;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void onOK() {
        SourceDialogView view = getView();
        URLProtoResolver resolver = service.getURLResolver(txtURI.getText());
        if (resolver == null) {
            view.alert("提示","不支持的url格式，无法打开", Alert.AlertType.ERROR).showAndWait();
            return;
        }
        File tempFile = resolver.load(txtURI.getText());
        if (tempFile == null) {
            return;
        }
        List<StorageFactory> factories = service.getAllExternalStorage(null);
        for (StorageFactory factory: factories) {
            if (!factory.support(tempFile)) {
                continue;
            }
            // 加载数据
            ArticleSetView articleSetView = getView().getView(ArticleSetView.class);
            articleSetView.loadContent(factory,tempFile);
            articleSetView.show();
            return;
        }
        List<SingleStorage> singleStores = service.getSingleStore(null);
        for (SingleStorage singleStorage: singleStores) {
            if (!singleStorage.support(tempFile)) {
                continue;
            }
            CompletableFuture.supplyAsync(() -> singleStorage.load(tempFile))
                    .whenCompleteAsync((article,e) -> {
                        if (e != null) {
                            logger.error("fail to load article " + tempFile.getName());
                            return;
                        }
                        Platform.runLater(() -> {
                            ReaderView readerView = getView().getView(ReaderView.class);
                            readerView.addArticle(article);
                            readerView.show();
                        });
                    });
            return;
        }
        view.alert("提示","暂不支持此格式，无法读取", Alert.AlertType.ERROR).showAndWait();
    }

    @FXML
    public void onCancel() {
        txtURI.setText("");
        SourceDialogView view = getView();
        view.hide();
    }

    @FXML
    public void openFiles() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("所有支持的格式","*.*"));
        chooser.setTitle("打开");
        File file = chooser.showOpenDialog(null);
        if (file == null) {
            return;
        } else {
            try {
                txtURI.setText(file.toURI().toURL().toExternalForm());
            } catch (Exception e) {
            }
        }
    }

    public String getURI() {
        return txtURI.getText();
    }

}
