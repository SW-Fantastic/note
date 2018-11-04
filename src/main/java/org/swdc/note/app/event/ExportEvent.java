package org.swdc.note.app.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleType;

/**
 * 导出事件，文档或者分类的导出，会发送此事件。
 */
public class ExportEvent extends ApplicationEvent{

    public ExportEvent(Object source) {
        super(source);
    }

    public boolean isTypeExport(){
        return source instanceof ArticleType;
    }

    public boolean isArtleExport(){
        return source instanceof Article;
    }

    public Article getArticle(){
        if(isArtleExport()){
            return  (Article)source;
        }
        return null;
    }

    public ArticleType getArticleType(){
        if(isTypeExport()){
            return (ArticleType)source;
        }
        return null;
    }

}
