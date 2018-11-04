package org.swdc.note.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.swdc.note.app.entity.ArticleType;

import java.util.List;

/**
 * Created by lenovo on 2018/9/4.
 */
@Repository
public interface ArticleTypeRepository extends JpaRepository<ArticleType,Long>{

    @Query("FROM ArticleType where parentType = NULL")
    List<ArticleType> getTopLevelType();

}
