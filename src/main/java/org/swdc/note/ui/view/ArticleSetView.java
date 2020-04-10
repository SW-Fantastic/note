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
import org.swdc.note.ui.controllers.ArticleSetController;

@Scope(ScopeType.MULTI)
@View(title = "文档集",background = true,resizeable = true)
public class ArticleSetView extends FXView {

    @Aware
    private MaterialIconsService iconsService = null;

    @Override
    public void initialize() {
        Stage stage = getStage();
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        stage.setWidth(stage.getMinWidth());
        stage.setHeight(stage.getMinHeight());
        this.initViewToolButton("btnexport","unarchive");
        this.initViewToolButton("btnimport", "archive");
        this.initViewToolButton("btnimportAll", "move_to_inbox");
    }

    public void loadContent(ArticleType articleSet) {
        ArticleSetController controller = getLoader().getController();
        controller.loadArticleType(articleSet);
    }

    private void initViewToolButton(String id, String icon) {
        Button btn = findById(id);
        if (btn == null) {
            return;
        }
        btn.setPadding(new Insets(4,4,4,4));
        btn.setFont(iconsService.getFont(FontSize.MIDDLE_SMALL));
        btn.setText(iconsService.getFontIcon(icon));
    }

}
