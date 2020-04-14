package org.swdc.note.ui.events;

import org.swdc.fx.AppComponent;
import org.swdc.fx.event.AppEvent;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;

public class RefreshEvent extends AppEvent<ArticleType> {

    private Article article;

    public RefreshEvent(ArticleType type , AppComponent source) {
        super(type, source);
    }

    public RefreshEvent(Article article, AppComponent source) {
        this(article.getType(), source);
        this.article = article;
    }

    public Article getArticle() {
        return article;
    }
}
