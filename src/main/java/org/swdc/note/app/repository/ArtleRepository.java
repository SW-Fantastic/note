package org.swdc.note.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swdc.note.app.entity.Artle;

import java.util.List;

/**
 * 文档的数据操作类
 */
@Repository
public interface ArtleRepository extends JpaRepository<Artle,Long> {

    List<Artle> findByTitleContaining(String key);

}
