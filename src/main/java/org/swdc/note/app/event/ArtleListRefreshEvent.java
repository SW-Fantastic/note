package org.swdc.note.app.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.note.app.entity.ArtleType;

/**
 * 文档刷新事件，添加了新的文档，或文档类型发生变化，应当刷新文档列表的时候
 * 需要发送此事件
 */
public class ArtleListRefreshEvent extends ApplicationEvent {

    public ArtleListRefreshEvent(ArtleType source) {
        super(source);
    }

    public ArtleType getModifyedType(){
        return (ArtleType) source;
    }

}
