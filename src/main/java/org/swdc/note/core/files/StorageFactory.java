package org.swdc.note.core.files;

import org.swdc.note.core.files.storages.AbstractArticleStorage;

public interface StorageFactory {

    AbstractArticleStorage getTypeStorage();

    String getName();

}
