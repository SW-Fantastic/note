package org.swdc.note.core.proto;

import org.swdc.fx.AppComponent;


import java.io.File;

public abstract class URLProtoResolver extends AppComponent {


    public abstract File load(String url);

    public abstract boolean support(String url);

}
