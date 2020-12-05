package org.swdc.note.core.files;

import javafx.stage.FileChooser;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;

import java.io.File;
import java.util.List;

public interface ExternalStorage {

    /**
     * 打开文件，做好读取数据的全部准备
     * @param file 目标文件
     */
    void open(File file);

    /**
     * 关闭文档，清理数据
     */
    void close();

    /**
     * 加载顶级分类
     * @return 所有顶级分类。
     */
    List<ArticleType> loadContents();

    /**
     * 添加分类，将分类保存至文件。
     * 依次处理分类的所有子分类，分类内部所有文档。
     * @param type 分类
     * @return Type的id
     */
    String addType(ArticleType type);

    /**
     * 增加文档。
     * 文档应当包含分类信息。
     * 如果文档指定的分类不存在，那么创建他。
     * @param article 文档。
     * @return Article的Id
     */
    String addArticle(Article article);

    /**
     * 删除文档
     * @param article
     */
    void deleteArticle(Article article);

    /**
     * 删除分类
     * @param type
     */
    void deleteType(ArticleType type);

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

}
