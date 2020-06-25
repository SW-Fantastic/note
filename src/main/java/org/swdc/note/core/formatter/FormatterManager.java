package org.swdc.note.core.formatter;

import org.swdc.fx.container.DefaultContainer;

public class FormatterManager extends DefaultContainer<CommonContentFormatter> {

    @Override
    public boolean isComponentOf(Class aClass) {
        return ContentFormatter.class.isAssignableFrom(aClass);
    }

}
