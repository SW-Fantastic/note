package org.swdc.note.core.files;

import org.swdc.dependency.annotations.ImplementBy;
import org.swdc.note.core.files.factory.NoSqlStorageFactory;

import java.io.File;

/**
 * 文档的一个或者多个分类的导出文件可以通过本类进行读写，
 * 例如从里面读取分类，读取分类里面的文档以及导入和导出等。
 */
@ImplementBy({
        NoSqlStorageFactory.class
})
public interface StorageFactory {

    ExternalStorage getTypeStorage();

    String getName();

    boolean support(File file);

}
