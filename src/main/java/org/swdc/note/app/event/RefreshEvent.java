package org.swdc.note.app.event;

import org.springframework.context.ApplicationEvent;

public class RefreshEvent extends ApplicationEvent {

    public RefreshEvent(Class form) {
        super(form);
    }

}
