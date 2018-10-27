package org.swdc.note.app.event;

import org.springframework.context.ApplicationEvent;

/**
 * 在进行一些操作之后需要重置view，
 * 此事件用来引发组件的重置。
 */
public class ResetEvent extends ApplicationEvent {

    public ResetEvent(Class source) {
        super(source);
    }
}
