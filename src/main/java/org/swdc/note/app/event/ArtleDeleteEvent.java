package org.swdc.note.app.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.note.app.entity.Artle;

/**
 *  文档删除事件，删除的时候，view会发布此事件，
 *  controller根据事件携带的对象进行删除。
 */
public class ArtleDeleteEvent extends ApplicationEvent {

    public ArtleDeleteEvent(Artle source) {
        super(source);
    }

    public Artle getArtle(){
        return (Artle)source;
    }

}
