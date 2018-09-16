package org.swdc.note.app.event;

import org.swdc.note.app.entity.Artle;

/**
 * Created by lenovo on 2018/9/16.
 */
public class ArtleEditEvent extends ViewChangeEvent{

    private Artle changedArtle;

    public ArtleEditEvent(Artle source) {
        super("EditView");
        this.changedArtle = source;
    }

    public Artle getSource(){
        return this.changedArtle;
    }
}
