package org.swdc.note.config;

import org.swdc.config.AbstractConfig;
import org.swdc.config.annotations.ConfigureSource;
import org.swdc.config.annotations.Property;
import org.swdc.config.configs.PropertiesHandler;
import org.swdc.fx.config.PropEditor;
import org.swdc.fx.config.editors.CheckEditor;
import org.swdc.fx.config.editors.NumberEditor;

@ConfigureSource(value = "assets/render.properties", handler = PropertiesHandler.class)
public class RenderConfig extends AbstractConfig {



    @PropEditor(
            editor = NumberEditor.class,
            name = "标题大小",
            description = "标题大小",
            resource = "12-48"
    )
    @Property("markdown.headerFontSize")
    private Integer headerFontSize = 24;

    @PropEditor(
            editor = NumberEditor.class,
            name = "文本大小",
            description = "文本大小",
            resource = "12-48"
    )
    @Property("markdown.renderFontSize")
    private Integer renderFontSize = 14;

    @PropEditor(
            editor = CheckEditor.class,
            name = "渲染字体阴影",
            description = "渲染字体阴影"
    )
    @Property("markdown.textShadow")
    private Boolean textShadow = false;

    public Boolean getTextShadow() {
        return textShadow;
    }

    public void setTextShadow(Boolean textShadow) {
        this.textShadow = textShadow;
    }

    public void setHeaderFontSize(Integer headerFontSize) {
        this.headerFontSize = headerFontSize;
    }

    public Integer getHeaderFontSize() {
        return headerFontSize;
    }

    public void setRenderFontSize(Integer renderFontSize) {
        this.renderFontSize = renderFontSize;
    }

    public Integer getRenderFontSize() {
        return renderFontSize;
    }
}
