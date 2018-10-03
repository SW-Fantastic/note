package org.swdc.note.app.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.entity.ArtleType;

/**
 *  文档删除事件，删除的时候，view会发布此事件，
 *  controller根据事件携带的对象进行删除。
 */
public class DeleteEvent extends ApplicationEvent {

    public DeleteEvent(Artle source) {
        super(source);
    }

    public DeleteEvent(ArtleType type){
        super(type);
    }

    public boolean isArtleDel(){
        return source instanceof Artle;
    }

    public boolean isArtleTypeDel(){
     return source instanceof ArtleType;
    }

    public Artle getArtle(){
        if(isArtleDel()){
            return (Artle)source;
        }
        return null;
    }

    public ArtleType getArtleType(){
        if(isArtleTypeDel()){
            return (ArtleType)source;
        }
        return null;
    }

}
