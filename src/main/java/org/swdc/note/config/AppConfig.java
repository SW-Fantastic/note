package org.swdc.note.config;

import lombok.Getter;
import lombok.Setter;
import org.swdc.fx.anno.ConfigProp;
import org.swdc.fx.anno.PropType;
import org.swdc.fx.anno.Properties;
import org.swdc.fx.properties.DefaultUIConfigProp;

@Properties(value = "config.properties", prefix = "app")
public class AppConfig extends DefaultUIConfigProp {

    @Getter
    @Setter
    @ConfigProp(type = PropType.FOLDER_SELECT_IMPORTABLE,
            name = "主题", value = "assets/theme",
            tooltip = "系统的主题", propName = "theme")
    private String theme;

}
