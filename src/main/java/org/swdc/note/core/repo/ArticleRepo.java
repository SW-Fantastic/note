package org.swdc.note.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;

import java.util.List;

@Repository
public interface ArticleRepo extends JPARepository<Article, String> {

    @SQLQuery("FROM Article WHERE type.id = :type")
    List<Article> findByType(@Param("type") String type);

    @SQLQuery(value = "FROM Article ORDER BY createDate DESC",maxResult = 7, firstResult = 0)
    List<Article> findRecently();

    @SQLQuery(" FROM Article WHERE title like :title")
    List<Article> searchByTitle(@Param(value = "title", searchBy = true) String title);

}
