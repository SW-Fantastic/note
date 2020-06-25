package org.swdc.note.core.formatter;

import javafx.stage.FileChooser;

import java.nio.file.Path;

/**
 * 文档格式化接口。
 *
 * 负责将文档导出为具体的某种格式的文件，
 * 或者从某种格式的文件读取为文档对象。
 *
 * @param <T> 文档对象的类型
 */
public interface ContentFormatter<T> {

    /**
     * 文档对象存储为文件
     * @param path 存储的位置
     * @param entity 文档对象
     */
    void save(Path path, T entity);

    /**
     * 删除文档对象
     * @param path 文档对象的位置
     */
    void remove(Path path);

    /**
     * 此位置加载文档对象
     * @param path 文档的位置
     * @return
     */
    T load(Path path);

    /**
     * 本格式处理器是否支持此文件类型
     * @param path 文档路径
     * @return
     */
    boolean support(Path path);

    /**
     * 是否支持读取
     * @return
     */
    boolean readable();

    /**
     * 是否支持写入
     * @return
     */
    boolean writeable();

    /**
     * 获取ExtensionsFilter
     * @return
     */
    FileChooser.ExtensionFilter getExtensionFilter();

    /**
     * 此文档格式化处理器的名称
     * @return
     */
    String getName();

    /**
     * 文档的后缀名
     * @return
     */
    String getExtension();

    /**
     * 支持的文档对象的类型。
     * @return
     */
    Class<T> getType();

}
