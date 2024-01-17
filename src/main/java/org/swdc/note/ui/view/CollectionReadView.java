package org.swdc.note.ui.view;

import javafx.stage.Stage;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.core.entities.CollectionArticle;
import org.swdc.note.ui.controllers.CollectionReadViewController;

@View(title = "阅读",viewLocation = "views/main/CollectionReaderView.fxml")
public class CollectionReadView extends AbstractView {


    public void addArticle(CollectionArticle article) {
        CollectionReadViewController controller = getController();
        controller.addArticle(article);
    }


    @Override
    public void show() {
        Stage stage = getStage();
        if (stage.isShowing()) {
            stage.toFront();
        } else {
            stage.show();
        }
    }



}
