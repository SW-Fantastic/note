package org.swdc.note.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swdc.note.app.entity.ArticleContext;

/**
 * 文档内容的数据操作类
 */
@Repository
public interface ArticleContextRepository extends JpaRepository<ArticleContext,Long> {
}
