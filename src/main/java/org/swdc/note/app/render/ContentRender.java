package org.swdc.note.app.render;

import java.util.Map;

public interface ContentRender {

    String processBeforeRender(String source, Map<String,String> resource);

    String renderSource(String source);

    String processAfterRender(String html);

    boolean support(String subfix);

    default String render(String content, Map<String,String> images) {
        String data = this.processBeforeRender(content,images);
        data = this.renderSource(data);
        return this.processAfterRender(data);
    }

}
