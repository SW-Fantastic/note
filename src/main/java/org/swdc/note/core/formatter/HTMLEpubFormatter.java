package org.swdc.note.core.formatter;

import org.swdc.note.core.entities.ArticleType;

import java.nio.file.Path;

public class HTMLEpubFormatter extends CommonContentFormatter<ArticleType> {

    @Override
    public void save(Path path, ArticleType entity) {

    }

    @Override
    public ArticleType load(Path path) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getExtension() {
        return "epub";
    }

    @Override
    public Class<ArticleType> getType() {
        return ArticleType.class;
    }
}
