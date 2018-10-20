package org.swdc.note.app.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.note.app.file.FileFormater;

import java.io.File;

/**
 * 处理以分类为单位的导入
 */
public class TypeImportEvent extends ApplicationEvent{

    private File targetFile;

    private FileFormater formatter;

    public TypeImportEvent(File file,FileFormater formatter) {
        super(file);
        this.targetFile = file;
        this.formatter = formatter;
    }

    public File getTargetFile() {
        return targetFile;
    }

    public FileFormater getFormatter() {
        return formatter;
    }

}
