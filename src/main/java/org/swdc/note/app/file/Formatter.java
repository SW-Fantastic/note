package org.swdc.note.app.file;

import java.io.File;

public interface Formatter <T> {

    boolean supportObject(Class type);

    boolean supportExtension(String extension);

    T readDocument(File file);

    void writeDocument(File file, T target);

    String getFormatName();

    String getFormatExtension();

    boolean isBatch();

}
