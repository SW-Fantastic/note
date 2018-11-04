package org.swdc.note.app.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleType;

import java.util.List;

/**
 * 文档刷新事件，添加了新的文档，或文档类型发生变化，应当刷新文档列表的时候
 * 需要发送此事件
 */
public class ArticleListRefreshEvent extends ApplicationEvent {

    /**
     * 按照类型刷新列表
     * @param source 分类
     */
    public ArticleListRefreshEvent(ArticleType source) {
        super(source);
    }

    /**
     * 按照实体刷新列表
     * @param list 实体list
     */
    public ArticleListRefreshEvent(List<Article> list){
        super(list);
    }

    public boolean isTypeRefresh(){
        return source instanceof ArticleType;
    }

    public boolean isItemRefresh(){
        return source instanceof List;
    }

    public ArticleType getModifyedType(){
        if(isTypeRefresh()){
            return (ArticleType) source;
        }
        return null;
    }

    public List<Article> getRefList(){
        if(isItemRefresh()){
            return (List<Article>)source;
        }
        return null;
    }

}
