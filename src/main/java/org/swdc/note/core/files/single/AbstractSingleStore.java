package org.swdc.note.core.files.single;

import org.swdc.dependency.annotations.ImplementBy;
import org.swdc.note.core.files.SingleStorage;


@ImplementBy({
        HTMLSingleStore.class,
        SourceSingleStore.class
})
public abstract class AbstractSingleStore  implements SingleStorage {

}
