package org.swdc.note.app.event;

import org.springframework.context.ApplicationEvent;

/**
 * Created by lenovo on 2019/5/17.
 */
public class ReLaunchEvent extends ApplicationEvent {
    public ReLaunchEvent(Object source) {
        super(source);
    }
}
