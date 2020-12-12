package org.swdc.note.core.files;

import javafx.stage.FileChooser;
import org.swdc.note.core.entities.Article;

import java.io.File;

public interface SingleStorage {

    /**
     * 返回FileChooser能够使用的文件过滤器。
     * @return
     */
    FileChooser.ExtensionFilter getFilter();

    /**
     * 返回文件类型名称。
     * @return
     */
    String getFileTypeName();

    /**
     * 获取拓展名
     * @return
     */
    String getExtension();

    /**
     * 保存文件
     * @param article
     * @param target
     */
    void save(Article article, File target);

    /**
     * 加载文件
     * @param file
     * @return
     */
    Article load(File file);

    /**
     * 是否支持此文件
     * @param file
     * @return
     */
    boolean support(File file);

}
