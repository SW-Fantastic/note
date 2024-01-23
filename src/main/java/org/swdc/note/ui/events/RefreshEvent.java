package org.swdc.note.ui.events;

import org.swdc.dependency.event.AbstractEvent;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.entities.CollectionType;

public class RefreshEvent extends AbstractEvent {

    private Article article;

    private RefreshType type;

    private ArticleType articleType;

    private CollectionType collectionType;

    public RefreshEvent(ArticleType type , Object source, RefreshType refreshType) {
        super(source);
        this.type = refreshType;
        this.articleType = type;
    }

    public RefreshEvent(Article article, Object source, RefreshType type) {
        this(article.getType(), source,type);
        this.article = article;
    }

    public RefreshEvent(CollectionType type, Object source, RefreshType refreshType) {
        super(source);
        this.type = refreshType;
        this.collectionType = type;
    }

    public RefreshType getType() {
        return type;
    }

    public ArticleType getArticleType() {
        return articleType;
    }

    public Article getArticle() {
        return article;
    }

    public CollectionType getCollectionType() {
        return collectionType;
    }
}
