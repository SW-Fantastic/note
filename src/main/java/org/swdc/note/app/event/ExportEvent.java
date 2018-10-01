package org.swdc.note.app.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.entity.ArtleType;

/**
 * 导出事件，文档或者分类的导出，会发送此事件。
 */
public class ExportEvent extends ApplicationEvent{

    public ExportEvent(Object source) {
        super(source);
    }

    public boolean isTypeExport(){
        return source instanceof ArtleType;
    }

    public boolean isArtleExport(){
        return source instanceof Artle;
    }

    public Artle getArtle(){
        if(isArtleExport()){
            return  (Artle)source;
        }
        return null;
    }

    public ArtleType getArtleType(){
        if(isTypeExport()){
            return (ArtleType)source;
        }
        return null;
    }

}
