package org.swdc.note.ui.controllers;

import jakarta.inject.Inject;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.swdc.fx.view.ViewController;
import org.swdc.note.core.entities.CollectionFocus;
import org.swdc.note.core.service.CollectionService;
import org.swdc.note.ui.view.dialogs.CollectionFocusView;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CollectFocusController extends ViewController<CollectionFocusView> {

    @FXML
    private TextField txtHost;

    @FXML
    private TextField txtXPath;

    @FXML
    private TableView<CollectionFocus> focusTable;

    @FXML
    private TableColumn<CollectionFocus,String> colHost;

    @FXML
    private TableColumn<CollectionFocus,String> colMatcher;

    @FXML
    private TableColumn<CollectionFocus,String> colSelector;

    @Inject
    private CollectionService collectionService;

    @Override
    protected void viewReady(URL url, ResourceBundle resourceBundle) {
        CollectionFocusView view = getView();
        txtHost.textProperty().addListener(v -> {
            view.addable(
                    !txtHost.getText().isBlank() &&
                    !txtXPath.getText().isBlank()
            );
        });
        txtXPath.textProperty().addListener(v -> {
            view.addable(
                    !txtHost.getText().isBlank() &&
                    !txtXPath.getText().isBlank()
            );
        });
        view.addable(false);
        view.removable(false);

        colHost.setCellValueFactory(new PropertyValueFactory<>("host"));
        colMatcher.setCellValueFactory(new PropertyValueFactory<>("urlMatch"));
        colSelector.setCellValueFactory(new PropertyValueFactory<>("selector"));

        ObservableList<CollectionFocus> focusItems = focusTable.getItems();
        focusItems.clear();
        focusItems.addAll(collectionService.getFocuses());

        focusTable.getSelectionModel().selectedItemProperty().addListener(this::onSelectionChanged);
    }

    private void onSelectionChanged(Observable observable) {

        CollectionFocus focus = focusTable.getSelectionModel().getSelectedItem();
        getView().removable(focus != null);

    }

    @FXML
    public void addRule() {
        String url = txtHost.getText();
        try {
            if (url.endsWith("/")) {
                url = url.substring(0,url.length() - 1);
            }
            URI uri = new URI(url);
            String selector = txtXPath.getText();
            CollectionFocus focus = collectionService.saveFocus(uri.getHost(),uri.toURL().toExternalForm(),selector);

            ObservableList<CollectionFocus> focusItems = focusTable.getItems();
            Optional<CollectionFocus> exists = focusItems
                    .stream()
                    .filter(f -> f.getId().equals(focus.getId()))
                    .findAny();
            if (exists.isEmpty()) {
                focusItems.add(focus);
            } else {
                exists.get().setSelector(selector);
            }
        } catch (MalformedURLException | URISyntaxException e) {
            Alert alert = getView().alert("失败","输入的URL无效。", Alert.AlertType.ERROR);
            alert.showAndWait();
        }
    }

    @FXML
    public void removeRule() {

        CollectionFocus focus = focusTable.getSelectionModel().getSelectedItem();
        if (focus == null) {
            return;
        }
        collectionService.removeFocus(focus);
        ObservableList<CollectionFocus> focusItems = focusTable.getItems();
        focusItems.clear();
        focusItems.addAll(collectionService.getFocuses());
    }

}
