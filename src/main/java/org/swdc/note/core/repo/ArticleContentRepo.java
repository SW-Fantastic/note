package org.swdc.note.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Repository;
import org.swdc.note.core.entities.ArticleContent;

@Repository
public interface ArticleContentRepo extends JPARepository<ArticleContent, Long> {
}
