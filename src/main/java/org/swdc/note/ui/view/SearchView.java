package org.swdc.note.ui.view;

import jakarta.annotation.PostConstruct;
import javafx.stage.Stage;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.ui.controllers.SearchViewController;

@View(title = "搜索",viewLocation = "views/main/SearchView.fxml")
public class SearchView extends AbstractView {

    @PostConstruct
    public void initialize() {
        Stage stg = getStage();
        stg.setMinWidth(673);
        stg.setMinHeight(476);
        this.getStage().setOnCloseRequest(e -> {
            SearchViewController controller = getController();
            controller.clear();
        });
    }

    public void search(String text) {
        SearchViewController controller = getController();
        controller.search(text);
    }

    public void clear() {
        SearchViewController controller = getController();
        controller.clear();
    }

}
