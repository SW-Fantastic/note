package org.swdc.note.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.entities.CollectionType;

import java.util.List;

@Repository
public interface CollectionTypeRepo extends JPARepository<CollectionType,String> {

    @SQLQuery("FROM CollectionType Where name = :name")
    List<CollectionType> findByTypeName(@Param("name") String name);

    @SQLQuery("FROM CollectionType Where parent = null")
    List<CollectionType> findRootTypes();

}
