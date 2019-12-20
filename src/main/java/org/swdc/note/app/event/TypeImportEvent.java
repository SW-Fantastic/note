package org.swdc.note.app.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.file.Formatter;

import java.io.File;

/**
 * 处理以分类为单位的导入
 */
public class TypeImportEvent extends ApplicationEvent{

    private ArticleType type;

    public TypeImportEvent(ArticleType type) {
        super("");
        this.type = type;
    }

    public ArticleType getType() {
        return type;
    }
}
