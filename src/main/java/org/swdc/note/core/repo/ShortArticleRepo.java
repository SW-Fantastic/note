package org.swdc.note.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Repository;
import org.swdc.note.core.entities.ShortArticle;

@Repository
public interface ShortArticleRepo extends JPARepository<ShortArticle,String> {

}
