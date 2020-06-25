package org.swdc.note.core.render;


import org.swdc.fx.container.DefaultContainer;

public class RendersManager extends DefaultContainer<ContentRender> {

    @Override
    public boolean isComponentOf(Class clazz) {
        return ContentRender.class.isAssignableFrom(clazz);
    }

}
