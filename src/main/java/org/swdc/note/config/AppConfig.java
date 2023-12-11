package org.swdc.note.config;

import org.swdc.config.annotations.ConfigureSource;
import org.swdc.config.annotations.Property;
import org.swdc.config.configs.PropertiesHandler;
import org.swdc.fx.config.ApplicationConfig;
import org.swdc.fx.config.PropEditor;
import org.swdc.fx.config.editors.CheckEditor;
import org.swdc.note.ui.component.KeyboardPropertyEditor;

@ConfigureSource(value = "assets/config.properties",handler = PropertiesHandler.class)
public class AppConfig extends ApplicationConfig {


    @PropEditor(
            editor = KeyboardPropertyEditor.class,
            name="快速编辑",
            description = "快速打开编辑器"
    )
    @Property("fastEditKey")
    private String fastEditKey;

    @PropEditor(
            editor = CheckEditor.class,
            name = "显示主窗口",
            description= "在启动后显示主窗口"
    )
    @Property("showMainView")
    private Boolean showMainView;

    @PropEditor(
            editor = CheckEditor.class,
            name = "自动提示",
            description = "在编辑器中使用自动提示"
    )
    @Property("enableAutoTip")
    private Boolean enableAutoTip;

    public Boolean getEnableAutoTip() {
        return enableAutoTip;
    }

    public void setEnableAutoTip(Boolean enableAutoTip) {
        this.enableAutoTip = enableAutoTip;
    }

    public Boolean getShowMainView() {
        return showMainView;
    }

    public void setShowMainView(Boolean showMainView) {
        this.showMainView = showMainView;
    }

    public String getFastEditKey() {
        return fastEditKey;
    }

    public void setFastEditKey(String fastEditKey) {
        this.fastEditKey = fastEditKey;
    }


}
