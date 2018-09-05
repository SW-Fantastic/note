package org.swdc.note.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.swdc.note.app.entity.ArtleType;

import java.util.List;

/**
 * Created by lenovo on 2018/9/4.
 */
@Repository
public interface ArtleTypeRepository extends JpaRepository<ArtleType,Long>{

    @Query("FROM ArtleType where parentType = NULL")
    List<ArtleType> getTopLevelType();

}
