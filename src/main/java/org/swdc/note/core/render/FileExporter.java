package org.swdc.note.core.render;

import javafx.stage.FileChooser;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;

import java.io.File;
import java.nio.file.Path;

/**
 * 文件处理接口
 */
public interface FileExporter {

    /**
     * 文件后缀，被导出的文件的后缀，单个文档
     * @return 后缀
     */
    String subfix();

    /**
     * 文件后缀，整个分类导出的时候使用。
     * @return 分类导出的后缀
     */
    String typeSubfix();

    /**
     * 导出器名称
     * @return 在ui中展示的单篇文档导出器的名称
     */
    String name();

    /**
     * 分类导出器名称
     * @return 在ui展示的分类导出的名称
     */
    String typeName();

    /**
     * 检查是否支持此文件的此操作
     * @param file 文件
     * @param type 是否要处理一个分类
     * @param writeOnly 是否仅用于导出
     * @return 是否支持当前操作
     */
    default boolean support(File file, boolean type, boolean writeOnly) {
        return file != null && support(file.toPath(), type, writeOnly);
    }

    /**
     * 检查是否支持此文件的此操作
     * @param path 文件
     * @param type 是否要处理一个分类
     * @param writeOnly 是否仅用于导出
     * @return 是否支持当前操作
     */
    default boolean support(Path path, boolean type, boolean writeOnly) {
        return path != null &&
                (type ? path.toString().endsWith(typeSubfix()) :path.toString().endsWith(subfix())) &&
                (writeOnly || readable(type));
    }

    /**
     * 导出文件的ui的Filter，文件名过滤器
     * @return 文件名过滤器
     */
    default FileChooser.ExtensionFilter extensionFilters() {
        return new FileChooser.ExtensionFilter(name(),"*." + subfix());
    }

    /**
     * 导出分类的文件名过滤器
     * @return 分类的文件名过滤器
     */
    default FileChooser.ExtensionFilter typeFilter() {
        return new FileChooser.ExtensionFilter(typeName(),"*." + typeSubfix());
    }

    /**
     * 是否支持文件的读取
     * @param type 被读取的文件是否为整个分类导出的文件
     * @return 是否支持读取
     */
    boolean readable(boolean type);

    /**
     * 写入文件
     * @param article 文档对象
     * @param file 目标文件
     */
    default void writeFile(Article article, File file){
        writeFile(article,file.toPath());
    }

    /**
     * 写入文件
     * @param article 文档对象
     * @param path 目标文件
     */
    void writeFile(Article article, Path path);

    /**
     * 将分类所有文档写入文件
     * @param type 分类对象
     * @param path 目标文件
     */
    default void writeType(ArticleType type, File path) {
        writeType(type,path.toPath());
    }

    /**
     * 将分类所有文档写入文件
     * @param type 分类对象
     * @param path 目标文件
     */
    void writeType(ArticleType type, Path path);

    /**
     * 读取文件
     * @param file 文件
     * @return 读取的文档对象
     */
    default Article readFile(File file) {
        return readFile(file.toPath());
    }

    /**
     * 读取文件
     * @param path 文件
     * @return 读取的文档对象
     */
    Article readFile(Path path);

    /**
     * 读取分类
     * @param file 分类导出的文件
     * @return 分类对象，包含所有的文档内容
     */
    default ArticleType readTypeFile(File file) {
        return readTypeFile(file.toPath());
    }

    /**
     * 读取分类
     * @param path 分类导出的文件
     * @return 分类对象，包含所有的文档内容
     */
    ArticleType readTypeFile(Path path);

}
