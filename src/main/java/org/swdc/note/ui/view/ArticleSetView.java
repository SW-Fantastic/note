package org.swdc.note.ui.view;

import jakarta.annotation.PostConstruct;
import javafx.stage.Stage;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.files.StorageFactory;
import org.swdc.note.ui.controllers.ArticleSetController;

import java.io.File;

@View(title = "文档集",multiple = true,viewLocation = "views/main/ArticleSetView.fxml")
public class ArticleSetView extends AbstractView {

    @PostConstruct
    public void initialize() {

        ArticleSetController controller = getController();

        Stage stage = getStage();
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        stage.setWidth(stage.getMinWidth());
        stage.setHeight(stage.getMinHeight());
        stage.setOnCloseRequest(e -> controller.closeArticleSet());
    }

    public void loadContent(StorageFactory factory, File file) {
        ArticleSetController controller = getController();
        controller.loadContent(factory,file);
    }

}
