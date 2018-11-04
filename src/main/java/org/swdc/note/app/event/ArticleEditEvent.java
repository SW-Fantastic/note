package org.swdc.note.app.event;

import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.Article;

/**
 * Created by lenovo on 2018/9/16.
 */
public class ArticleEditEvent extends ViewChangeEvent{

    private Article changedArticle;

    public ArticleEditEvent(Article source) {
        super("EditView");
        this.changedArticle = source;
    }

    public Article getSource(){
        return this.changedArticle;
    }
}
