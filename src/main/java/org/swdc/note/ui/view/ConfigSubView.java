package org.swdc.note.ui.view;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.View;
import org.swdc.note.config.AppConfig;
import org.swdc.note.config.RenderConfig;

@View(stage = false)
public class ConfigSubView extends FXView {

    @Override
    public void initialize() {
        AppConfig config = findComponent(AppConfig.class);
        TabPane tabPane = findById("tabs");

        Tab tabMainCfg = new Tab("默认配置");
        tabMainCfg.setContent(config.getEditor());
        tabPane.getTabs().add(tabMainCfg);

        RenderConfig renderConfig = findComponent(RenderConfig.class);
        Tab tabRenderCfg = new Tab("渲染配置");
        tabRenderCfg.setContent(renderConfig.getEditor());
        tabPane.getTabs().add(tabRenderCfg);
    }
}
