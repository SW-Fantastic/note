package org.swdc.note.core.files.factory;

import org.swdc.note.core.files.storages.AbstractArticleStorage;
import org.swdc.note.core.files.storages.NoSqlExtStorage;

import java.io.File;

public class NoSqlStorageFactory extends AbstractStorageFactory {

    @Override
    public AbstractArticleStorage getTypeStorage() {
        return findComponent(NoSqlExtStorage.class);
    }

    @Override
    public boolean support(File file) {
        return file.getName().endsWith("noteset");
    }

    @Override
    public String getName() {
        return "数据集";
    }

}
