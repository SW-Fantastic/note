package org.swdc.note.app.configs;

import java.io.File;

public interface Importer {

    void install(File file) throws Exception;

    String supportName();

    String[] extensions();

}
