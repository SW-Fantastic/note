package org.swdc.note.app.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleType;

/**
 *  文档删除事件，删除的时候，view会发布此事件，
 *  controller根据事件携带的对象进行删除。
 */
public class DeleteEvent extends ApplicationEvent {

    public DeleteEvent(Article source) {
        super(source);
    }

    public DeleteEvent(ArticleType type){
        super(type);
    }

    public boolean isArtleDel(){
        return source instanceof Article;
    }

    public boolean isArtleTypeDel(){
     return source instanceof ArticleType;
    }

    public Article getArtle(){
        if(isArtleDel()){
            return (Article)source;
        }
        return null;
    }

    public ArticleType getArtleType(){
        if(isArtleTypeDel()){
            return (ArticleType)source;
        }
        return null;
    }

}
