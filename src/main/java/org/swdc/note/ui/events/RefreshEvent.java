package org.swdc.note.ui.events;

import org.swdc.fx.AppComponent;
import org.swdc.fx.event.AppEvent;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;

public class RefreshEvent extends AppEvent<ArticleType> {

    private Article article;

    private RefreshType type;

    public RefreshEvent(ArticleType type , AppComponent source, RefreshType refreshType) {
        super(type, source);
        this.type = refreshType;
    }

    public RefreshEvent(Article article, AppComponent source, RefreshType type) {
        this(article.getType(), source,type);
        this.article = article;
    }

    public RefreshType getType() {
        return type;
    }

    public Article getArticle() {
        return article;
    }
}
