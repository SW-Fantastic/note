package org.swdc.note.core.render;

import org.swdc.dependency.annotations.ImplementBy;
import org.swdc.note.core.entities.Article;


@ImplementBy({
    HTMLRender.class
})
public abstract class ContentRender {

    public abstract String renderAsText(Article article);

    public abstract byte[] renderAsBytes(Article article);

}
