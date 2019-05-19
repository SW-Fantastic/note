package org.swdc.note.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swdc.note.app.entity.Article;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * 文档的数据操作类
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article,Long> {

    List<Article> findByTitleContaining(String key);

    default Article findNext(EntityManager em, Article article) {
        Query query = em.createQuery("FROM Article WHERE id > :id AND type = :typ ORDER BY id ASC");
        query.setParameter("id", article.getId());
        query.setParameter("typ", article.getType());
        query.setMaxResults(1);
        return (Article)query.getSingleResult();
    }

    default Article findPrev(EntityManager em, Article article) {
        Query query = em.createQuery("FROM Article WHERE id < :id AND type = :typ ORDER BY id DESC");
        query.setParameter("id", article.getId());
        query.setParameter("typ", article.getType());
        query.setMaxResults(1);
        return (Article)query.getSingleResult();
    }

}
