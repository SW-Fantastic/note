package org.swdc.note.core.files;

import org.swdc.dependency.annotations.ImplementBy;
import org.swdc.note.core.files.factory.NoSqlStorageFactory;

import java.io.File;

@ImplementBy({
        NoSqlStorageFactory.class
})
public interface StorageFactory {

    ExternalStorage getTypeStorage();

    String getName();

    boolean support(File file);

}
