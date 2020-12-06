package org.swdc.note.core.files;

import org.swdc.note.core.files.storages.AbstractArticleStorage;

import java.io.File;

public interface StorageFactory {

    AbstractArticleStorage getTypeStorage();

    String getName();

    boolean support(File file);

}
