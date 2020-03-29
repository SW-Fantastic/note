package org.swdc.note.config;

import lombok.Getter;
import lombok.Setter;
import org.swdc.fx.anno.ConfigProp;
import org.swdc.fx.anno.PropType;
import org.swdc.fx.anno.Properties;
import org.swdc.fx.properties.FXProperties;

@Properties(value = "render.properties", prefix = "markdown")
public class RenderConfig extends FXProperties {

    @Getter
    @Setter
    @ConfigProp(type= PropType.NUMBER_SELECTABLE,
            value = "48",tooltip = "标题大小",
            name = "标题大小",propName = "headerFontSize")
    private Integer headerFontSize = 24;

    @Getter
    @Setter
    @ConfigProp(type = PropType.NUMBER_SELECTABLE,
            name = "文本大小", value = "48",
            tooltip = "文本大小", propName = "renderFontSize")
    private Integer renderFontSize = 14;

    @Getter
    @Setter
    @ConfigProp(type = PropType.CHECK,
            name = "渲染字体阴影", value = "false",
            tooltip = "渲染字体阴影", propName = "textShadow")
    private Boolean textShadow = false;

}
