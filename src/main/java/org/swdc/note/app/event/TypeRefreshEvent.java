package org.swdc.note.app.event;

import org.springframework.context.ApplicationEvent;

/**
 * Created by lenovo on 2018/9/4.
 */
public class TypeRefreshEvent extends ApplicationEvent {

    public TypeRefreshEvent(Object source) {
        super(source);
    }

}
