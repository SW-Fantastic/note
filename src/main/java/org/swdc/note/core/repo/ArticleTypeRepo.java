package org.swdc.note.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.note.core.entities.ArticleType;

import java.util.List;

@Repository
public interface ArticleTypeRepo extends JPARepository<ArticleType, String> {

    @SQLQuery("FROM ArticleType Where name = :name")
    List<ArticleType> findByTypeName(@Param("name") String name);

    @SQLQuery("FROM ArticleType Where parent = null")
    List<ArticleType> findRootTypes();

}
