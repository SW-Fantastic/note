package org.swdc.note.core.files.factory;

import jakarta.inject.Inject;
import org.swdc.dependency.annotations.MultipleImplement;
import org.swdc.note.core.files.ExternalStorage;
import org.swdc.note.core.files.StorageFactory;
import org.swdc.note.core.files.storages.NoSqlExtStorage;

import java.io.File;

@MultipleImplement(StorageFactory.class)
public class NoSqlStorageFactory implements StorageFactory {

    @Inject
    private NoSqlExtStorage noSqlExtStorage;

    @Override
    public ExternalStorage getTypeStorage() {
        return noSqlExtStorage;
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
