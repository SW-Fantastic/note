package org.swdc.note.app.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.entity.ArtleType;

import java.util.List;

/**
 * 文档刷新事件，添加了新的文档，或文档类型发生变化，应当刷新文档列表的时候
 * 需要发送此事件
 */
public class ArtleListRefreshEvent extends ApplicationEvent {

    /**
     * 按照类型刷新列表
     * @param source 分类
     */
    public ArtleListRefreshEvent(ArtleType source) {
        super(source);
    }

    /**
     * 按照实体刷新列表
     * @param list 实体list
     */
    public ArtleListRefreshEvent(List<Artle> list){
        super(list);
    }

    public boolean isTypeRefresh(){
        return source instanceof ArtleType;
    }

    public boolean isItemRefresh(){
        return source instanceof List;
    }

    public ArtleType getModifyedType(){
        if(isTypeRefresh()){
            return (ArtleType) source;
        }
        return null;
    }

    public List<Artle> getRefList(){
        if(isItemRefresh()){
            return (List<Artle>)source;
        }
        return null;
    }

}
