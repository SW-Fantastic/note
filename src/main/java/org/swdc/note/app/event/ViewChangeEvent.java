package org.swdc.note.app.event;

import org.springframework.context.ApplicationEvent;

/**
 * 界面切换事件，界面需要进行切换的时候应该发送此事件
 * view监听到后进行处理，更改view
 */
public class ViewChangeEvent extends ApplicationEvent{

    public ViewChangeEvent(String source) {
        super(source);
    }

    public String getViewName(){
        return (String)source;
    }
}
