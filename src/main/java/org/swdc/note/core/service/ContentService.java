package org.swdc.note.core.service;

import org.dizitart.no2.*;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.swdc.fx.services.Service;
import org.swdc.note.core.entities.ArticleContent;

import java.io.File;
import java.util.List;

public class ContentService extends Service {

    private Nitrite documentDB;

    private ObjectRepository<ArticleContent> contentRepo;

    @Override
    public void initialize() {
        documentDB = Nitrite.builder()
                .compressed()
                .filePath(new File("./data/articleContent.db"))
                .openOrCreate();


        contentRepo = documentDB.getRepository(ArticleContent.class);
        if (!contentRepo.hasIndex("articleId")) {
            contentRepo.createIndex("articleId", IndexOptions.indexOptions(IndexType.Unique));
        }
    }

    public ArticleContent getArticleContent(String contentId) {
        List<ArticleContent> contents =  contentRepo.find(ObjectFilters.eq("articleId",contentId))
                .toList();
        if (contents.size() > 0) {
            return contents.get(0);
        } else {
            return null;
        }
    }

    public ArticleContent saveArticleContent(ArticleContent content) {
        documentDB.compact();
        if (content.getArticleId() == null) {
            return null;
        }
        if (getArticleContent(content.getArticleId()) == null) {
            int effect = contentRepo.insert(content).getAffectedCount();
            if (effect > 0) {
                return content;
            }
            return null;
        } else {
            int effect = contentRepo.update(content).getAffectedCount();
            if (effect > 0) {
                return content;
            }
            return null;
        }
    }

    public int removeContent(String articleId) {
        int effect = contentRepo.remove(ObjectFilters.eq("articleId",articleId))
                .getAffectedCount();
        documentDB.commit();
        documentDB.compact();
        return effect;
    }

    public int removeByType(String typeId) {
        int effect = contentRepo
                .remove(ObjectFilters.eq("typeId",typeId))
                .getAffectedCount();
        documentDB.commit();
        documentDB.compact();
        return effect;
    }

    @Override
    public void destroy() {
        documentDB.commit();
        documentDB.close();
    }
}
