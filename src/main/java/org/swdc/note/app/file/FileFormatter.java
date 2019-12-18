package org.swdc.note.app.file;

import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

/**
 * 文件格式接口，用于导入导出数据
 */
public abstract class FileFormatter {

    /**
     * 被处理的文件的类型名
     * @return 类型名
     */
    public abstract String getFormatName();

    /**
     * 支持的文件的拓展名
     * @param extension 文件拓展名
     * @return
     */
    public abstract boolean supportSingleExtension(String extension);

    /**
     * 支持的导入导出的拓展名
     * @param extension
     * @return
     */
    public abstract boolean supportMultiExtension(String extension);

    /**
     * 处理读取
     * @param target
     */
    public abstract <T> T processRead(File target,Class<T> clazz);

    /**
     * 处理写入
     * @param target
     */
    public abstract void processWrite(File target,Object targetObj);

    public abstract <T> void processImport(File target,T targetObj);

    public abstract List<FileChooser.ExtensionFilter> getFilters();

    public abstract boolean canRead();

    public abstract boolean canWrite();

    public String toString(){
        return this.getFormatName();
    }

}
