package org.swdc.note.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.note.core.entities.CollectionType;

import java.util.List;

@Repository
public interface CollectionTypeRepo extends JPARepository<CollectionType,String> {

    @SQLQuery("FROM CollectionType Where title = :title")
    List<CollectionType> findByTypeTitle(@Param("title") String title);

    @SQLQuery("FROM CollectionType Where title = :title and parent.id = :parent")
    List<CollectionType> findByTypeTitleAndParent(@Param("title") String title, @Param("parent") String parentId);

    @SQLQuery("FROM CollectionType Where parent = null")
    List<CollectionType> findRootTypes();

    @SQLQuery("FROM CollectionType Where title = :title")
    CollectionType findByHost(@Param("host") String host);

}
