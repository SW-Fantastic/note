package org.swdc.note.core;

import org.swdc.data.EMFProvider;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.entities.ArticleResource;
import org.swdc.note.core.entities.ArticleType;

import java.util.Arrays;
import java.util.List;

public class EntityManagerProviderImpl extends EMFProvider {
    @Override
    public List<Class> registerEntities() {
        return Arrays.asList(
                Article.class,
                ArticleContent.class,
                ArticleResource.class,
                ArticleType.class
        );
    }
}
