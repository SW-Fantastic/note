package org.swdc.note.app.event;

import lombok.Getter;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.Article;

/**
 * Created by lenovo on 2018/9/16.
 */
public class ArticleOpenEvent extends ViewChangeEvent {

    @Getter
    private Article article;

    public ArticleOpenEvent(Article source) {
        super("ReadView");
        article = source;
    }
}
