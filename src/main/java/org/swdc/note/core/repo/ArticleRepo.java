package org.swdc.note.core.repo;

import org.swdc.fx.jpa.JPARepository;
import org.swdc.fx.jpa.anno.Param;
import org.swdc.fx.jpa.anno.SQLQuery;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;

import java.util.List;

public interface ArticleRepo extends JPARepository<Article, Long> {

    @SQLQuery("FROM Article WHERE type = :type")
    List<Article> findByType(@Param("type") ArticleType type);

    @SQLQuery(value = "FROM Article ORDER BY createDate DESC",maxResult = 7, firstResult = 0)
    List<Article> findRecently();

    @SQLQuery(" FROM Article WHERE title like :title")
    List<Article> searchByTitle(@Param(value = "title", searchBy = true) String title);

}
