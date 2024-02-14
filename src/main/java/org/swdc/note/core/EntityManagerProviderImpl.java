package org.swdc.note.core;

import org.swdc.data.EMFProvider;
import org.swdc.note.core.entities.*;

import java.util.Arrays;
import java.util.List;

public class EntityManagerProviderImpl extends EMFProvider {
    @Override
    public List<Class> registerEntities() {
        return Arrays.asList(
                Article.class,
                ArticleContent.class,
                ArticleResource.class,
                ArticleType.class,
                CollectionType.class,
                CollectionArticle.class,
                CollectionFocus.class,
                ShortArticle.class,
                ShortArticleTag.class
        );
    }
}
