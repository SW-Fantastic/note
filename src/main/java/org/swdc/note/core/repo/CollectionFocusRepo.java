package org.swdc.note.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.note.core.entities.CollectionFocus;

import java.util.List;

@Repository
public interface CollectionFocusRepo extends JPARepository<CollectionFocus,Long> {

    @SQLQuery("FROM CollectionFocus where urlMatch = :match")
    CollectionFocus getCollectionFocusByMatch(@Param("match") String host);

    @SQLQuery("FROM CollectionFocus where host = :host")
    List<CollectionFocus> getCollectionFocusByHost(@Param("host") String host);

}
