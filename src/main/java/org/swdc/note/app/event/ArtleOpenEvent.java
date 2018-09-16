package org.swdc.note.app.event;

import lombok.Getter;
import org.swdc.note.app.entity.Artle;

/**
 * Created by lenovo on 2018/9/16.
 */
public class ArtleOpenEvent extends ViewChangeEvent {

    @Getter
    private Artle artle;

    public ArtleOpenEvent(Artle source) {
        super("ReadView");
        artle = source;
    }
}
