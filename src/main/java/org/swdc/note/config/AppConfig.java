package org.swdc.note.config;

import lombok.Getter;
import lombok.Setter;
import org.swdc.fx.anno.ConfigProp;
import org.swdc.fx.anno.PropType;
import org.swdc.fx.anno.Properties;
import org.swdc.fx.properties.DefaultUIConfigProp;
import org.swdc.note.ui.component.KeyboardPropertyEditor;

@Properties(value = "config.properties", prefix = "app")
public class AppConfig extends DefaultUIConfigProp {

    @Getter
    @Setter
    @ConfigProp(type = PropType.FOLDER_SELECT_IMPORTABLE,
            name = "主题", value = "assets/theme",
            tooltip = "系统的主题", propName = "theme")
    private String theme;

    @Getter
    @Setter
    @ConfigProp(type = PropType.CUSTOM,editor = KeyboardPropertyEditor.class,
            name="快速编辑",tooltip = "快速打开编辑器",
            propName = "fastEditKey", value = "")
    private String fastEditKey;

    @Getter
    @Setter
    @ConfigProp(type = PropType.CHECK, name = "显示主窗口",
            value = "", tooltip = "在启动后显示主窗口",
            propName = "showMainView")
    private Boolean showMainView;

    @Getter
    @Setter
    @ConfigProp(type = PropType.CHECK, name = "自动提示",
            value = "", tooltip = "在编辑器中使用自动提示",
            propName = "enableAutoTip")
    private Boolean enableAutoTip;
    
}
