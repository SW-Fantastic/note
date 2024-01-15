package org.swdc.note.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.note.core.entities.CollectionArticle;

import java.util.List;

@Repository
public interface CollectionRepo extends JPARepository<CollectionArticle, String> {

    @SQLQuery("FROM CollectionArticle where type.id = :typeId")
    List<CollectionArticle> findByType(@Param("typeId") String articleTypeId);

}
