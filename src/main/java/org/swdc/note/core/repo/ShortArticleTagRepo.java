package org.swdc.note.core.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.note.core.entities.ShortArticleTag;

import java.util.List;

@Repository
public interface ShortArticleTagRepo extends JPARepository<ShortArticleTag,Long> {

    @SQLQuery("from ShortArticleTag where name = :name")
    ShortArticleTag findByName(@Param("name")String name);

    @SQLQuery("from ShortArticleTag where name like :name")
    List<ShortArticleTag> searchByName(@Param(value = "name",searchBy = true) String name);

}
