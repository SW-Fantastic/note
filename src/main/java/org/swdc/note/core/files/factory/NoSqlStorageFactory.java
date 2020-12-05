package org.swdc.note.core.files.factory;

import org.swdc.note.core.files.storages.AbstractArticleStorage;
import org.swdc.note.core.files.storages.NoSqlExtStorage;

public class NoSqlStorageFactory extends AbstractStorageFactory {

    @Override
    public AbstractArticleStorage getTypeStorage() {
        return findComponent(NoSqlExtStorage.class);
    }

    @Override
    public String getName() {
        return "数据集";
    }

}
