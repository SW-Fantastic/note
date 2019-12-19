package org.swdc.note.app.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swdc.note.app.entity.Article;
import org.swdc.note.app.entity.ArticleContext;
import org.swdc.note.app.entity.ArticleType;
import org.swdc.note.app.file.Formatter;
import org.swdc.note.app.render.ContentRender;
import org.swdc.note.app.render.HTMLContentRender;
import org.swdc.note.app.repository.ArticleRepository;
import org.swdc.note.app.repository.ArticleTypeRepository;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.DataUtil;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 文章服务，提供关于文章的各种操作
 */
@Service
public class ArticleService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private List<ContentRender> renders;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleTypeRepository typeRepository;



    @Transactional
    public List<Article> loadArticles(ArticleType type){
        type = typeRepository.getOne(type.getId());
        Hibernate.initialize(type.getArticles());
        return type.getArticles();
    }

    @Transactional
    public ArticleContext loadContext(Article article){
        article = articleRepository.getOne(article.getId());
        Hibernate.initialize(article.getContext());
        return article.getContext();
    }

    @Transactional
    public Article saveArticle(Article article, ArticleContext context){
        if(article.getId() == null){
            article.setContext(context);
            return articleRepository.save(article);
        }
        Article articleOld = articleRepository.getOne(article.getId());
        Hibernate.initialize(articleOld.getContext());
        ArticleContext contextOld = articleOld.getContext();
        // 更新持久态对象
        contextOld = DataUtil.updateProperties(context,contextOld);
        contextOld.setImageRes(context.getImageRes());
        articleOld = DataUtil.updateProperties(article, articleOld);
        articleOld.setContext(contextOld);
        articleOld.setType(typeRepository.getOne(article.getType().getId()));

        return articleRepository.save(articleOld);
    }

    @Transactional
    public void deleteArticle(Article article){
        article = articleRepository.getOne(article.getId());
        articleRepository.delete(article);
    }

    @Transactional
    public List<Article> searchArticleByTitle(String key){
        return articleRepository.findByTitleContaining(key);
    }

    @Transactional
    public Article nextArticleOnType(Article offsetFrom) {
        try {
            return articleRepository.findNext(entityManager, offsetFrom);
        }catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    @Transactional
    public Article prevArticleOnType(Article offsetFrom) {
        try {
            return articleRepository.findPrev(entityManager, offsetFrom);
        }catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    public ContentRender getRender(String format) {
        for (ContentRender render : renders) {
            if (render.support(format)) {
                return render;
            }
        }
        return null;
    }

    public String renderHTML(ArticleContext context){
        ContentRender render = getRender("html");

        Map<String,String> resource = context.getImageRes();
        String contentStr = context.getContent();
        return render.render(contentStr,resource);
    }

}
