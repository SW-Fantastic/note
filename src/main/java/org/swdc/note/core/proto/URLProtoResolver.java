package org.swdc.note.core.proto;

import org.swdc.dependency.annotations.ImplementBy;

import java.io.File;

@ImplementBy({
        FileURLResolver.class,
        HttpURLResolver.class
})
public abstract class URLProtoResolver {


    public abstract File load(String url);

    public abstract boolean support(String url);

}
