package org.swdc.note.core.repo;

import org.swdc.fx.jpa.JPARepository;
import org.swdc.fx.jpa.anno.Param;
import org.swdc.fx.jpa.anno.SQLQuery;
import org.swdc.note.core.entities.ArticleType;

import java.util.List;

public interface ArticleTypeRepo extends JPARepository<ArticleType, String> {

    @SQLQuery("FROM ArticleType Where name = :name")
    List<ArticleType> findByTypeName(@Param("name") String name);

    @SQLQuery("FROM ArticleType Where parent = null")
    List<ArticleType> findRootTypes();

}
