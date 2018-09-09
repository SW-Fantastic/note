package org.swdc.note.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swdc.note.app.entity.ArtleContext;

/**
 * 文档内容的数据操作类
 */
@Repository
public interface ArtleContextRepository extends JpaRepository<ArtleContext,Long> {
}
