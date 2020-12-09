package org.swdc.note.ui.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Scope;
import org.swdc.fx.anno.ScopeType;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.MaterialIconsService;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.files.factory.AbstractStorageFactory;
import org.swdc.note.ui.controllers.ArticleSetController;

import java.io.File;

@Scope(ScopeType.MULTI)
@View(title = "文档集",background = true,resizeable = true)
public class ArticleSetView extends FXView {

    @Override
    public void initialize() {
        Stage stage = getStage();
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        stage.setWidth(stage.getMinWidth());
        stage.setHeight(stage.getMinHeight());
    }

    public void loadContent(AbstractStorageFactory factory, File file) {
        ArticleSetController controller = getLoader().getController();
        controller.loadContent(factory,file);
    }

}
