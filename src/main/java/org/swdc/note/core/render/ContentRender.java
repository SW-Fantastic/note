package org.swdc.note.core.render;

import org.swdc.fx.AppComponent;
import org.swdc.note.core.entities.Article;


public abstract class ContentRender extends AppComponent {

    public abstract String renderAsText(Article article);

    public abstract byte[] renderAsBytes(Article article);

}
