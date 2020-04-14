package org.swdc.note.ui.view;

import javafx.stage.Stage;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.View;
import org.swdc.note.ui.controllers.SearchViewController;

@View(title = "搜索",resizeable = true,background = true)
public class SearchView extends FXView {

    @Override
    public void initialize() {
        Stage stg = getStage();
        stg.setMinWidth(673);
        stg.setMinHeight(476);
        this.getStage().setOnCloseRequest(e -> {
            SearchViewController controller = getLoader().getController();
            controller.clear();
        });
    }

    public void search(String text) {
        SearchViewController controller = getLoader().getController();
        controller.search(text);
    }

    public void clear() {
        SearchViewController controller = getLoader().getController();
        controller.clear();
    }

}
