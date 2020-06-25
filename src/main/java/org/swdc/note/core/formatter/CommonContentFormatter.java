package org.swdc.note.core.formatter;

import javafx.stage.FileChooser;
import org.swdc.fx.AppComponent;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class CommonContentFormatter<T> extends AppComponent implements ContentFormatter<T> {

    /**
     * 是否可以读取文档
     * 具体的格式化处理器应该重写此方法、
     * @return
     */
    @Override
    public boolean readable() {
        return false;
    }

    /**
     * 是否可以写入文档
     * 具体的格式化处理器应该重写此方法。
     * @return
     */
    @Override
    public boolean writeable() {
        return false;
    }

    /**
     * 提供统一的文档删除操作。
     * @param path 文档对象的位置
     */
    @Override
    public void remove(Path path) {
        if (!Files.exists(path)) {
            return;
        }
        try {
            Files.delete(path);
        } catch (Exception e) {
            logger.error("fail to delete file");
        }
    }

    /**
     * 提供文档的打开对话框的类型filter。
     * @return
     */
    @Override
    public FileChooser.ExtensionFilter getExtensionFilter() {
        return new FileChooser.ExtensionFilter(getName(),"*."+getExtension());
    }

    /**
     * 根据后缀名判断是否支持，处理器可以重写此方法。
     * @param path 文档路径
     * @return
     */
    @Override
    public boolean support(Path path) {
        return path.toString().endsWith(this.getExtension());
    }

    /**
     * 供listView或者Combox显示的名称。
     * @return
     */
    @Override
    public String toString() {
        return getName();
    }

}
