package org.swdc.note.core.files.factory;

import org.swdc.fx.AppComponent;
import org.swdc.note.core.files.StorageFactory;

import java.io.File;

public abstract class AbstractStorageFactory extends AppComponent implements StorageFactory {

    @Override
    public boolean support(File file) {
        return false;
    }
}
