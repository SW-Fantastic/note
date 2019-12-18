package org.swdc.note.app.ui.controller.dialogs;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleContext;
import org.swdc.note.app.event.ArticleEditEvent;
import org.swdc.note.app.event.ResetEvent;
import org.swdc.note.app.service.ArticleService;
import org.swdc.note.app.ui.view.dialogs.ImageDialog;
import org.swdc.note.app.util.UIUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.*;

/**
 *  图片选择窗口
 */
@FXMLController
public class ImageDialogController implements Initializable{

    @Autowired
    private ImageDialog dlg;

    @FXML
    private ListView<String> listView;

    @FXML
    private ScrollPane scrPane;

    @Autowired
    private ArticleService articleService;

    @FXML
    protected void openImage() throws Exception{
        FileChooser chooser = new FileChooser();
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("图片",".jpg",".png",".bmp",".gif"));
        File file = chooser.showOpenDialog(dlg.getStage());
        Base64.Encoder enc = Base64.getEncoder();
        if(file!=null&& file.exists()){
            String res = enc.encodeToString(UIUtil.readFile(new FileInputStream(file)));
            if(dlg.getImages().keySet().contains(file.getName())){
                return;
            }
            dlg.getImages().put(file.getName(),res);
            listView.getItems().add(file.getName());
        }
    }

    @FXML
    protected void cancel(){
        if(dlg.getStage().isShowing()){
            dlg.getStage().close();
        }
    }

    @FXML
    protected void removeSelected(){
        Optional.ofNullable(listView.getSelectionModel().getSelectedItem()).ifPresent(name->{
            listView.getItems().remove(name);
            dlg.getImages().remove(name);
        });
    }

    @FXML
    protected void insertSelected(){
        Optional.ofNullable(listView.getSelectionModel().getSelectedItem()).ifPresent(name->{
            dlg.setSelectedImage(name);
            dlg.getStage().close();
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue!=null){
                Base64.Decoder dec = Base64.getDecoder();
                byte[] data = dec.decode(dlg.getImages().get(newValue));
                Image img = new Image(new ByteArrayInputStream(data));
                scrPane.setContent(new ImageView(img));
            }
        }));
    }

    @EventListener
    public void onArticleEdit(ArticleEditEvent e){
        Article article = e.getSource();
        ArticleContext context;
        if (!e.isContextFilled()) {
            context = articleService.loadContext(article);
        } else {
            context = article.getContext();
        }
        dlg.setImages(context.getImageRes());
        listView.getItems().clear();
        listView.getItems().addAll(context.getImageRes().keySet());
    }

    @EventListener
    public void onReset(ResetEvent event){
        dlg.getImages().clear();
        listView.getItems().clear();
    }

}
