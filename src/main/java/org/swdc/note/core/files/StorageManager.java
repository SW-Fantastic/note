package org.swdc.note.core.files;

import org.swdc.fx.container.DefaultContainer;
import org.swdc.note.core.files.factory.AbstractStorageFactory;
import org.swdc.note.core.files.single.AbstractSingleStore;
import org.swdc.note.core.files.storages.AbstractArticleStorage;

public class StorageManager extends DefaultContainer<AbstractStorageFactory> {

    @Override
    public boolean isComponentOf(Class aClass) {
        return AbstractStorageFactory.class.isAssignableFrom(aClass) ||
                AbstractArticleStorage.class.isAssignableFrom(aClass) ||
                AbstractSingleStore.class.isAssignableFrom(aClass);
    }

}
