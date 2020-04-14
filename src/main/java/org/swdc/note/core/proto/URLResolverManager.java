package org.swdc.note.core.proto;

import org.swdc.fx.DefaultContainer;

public class URLResolverManager extends DefaultContainer<URLProtoResolver> {

    @Override
    public boolean isComponentOf(Class aClass) {
        return URLProtoResolver.class.isAssignableFrom(aClass);
    }

}
