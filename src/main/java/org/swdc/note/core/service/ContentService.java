package org.swdc.note.core.service;

import org.dizitart.no2.*;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.swdc.fx.services.Service;
import org.swdc.note.core.entities.ArticleContent;

import java.io.File;

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

    public ArticleContent getArticleContent(Long contentId) {
        return contentRepo.find(ObjectFilters.eq("articleId",contentId))
                .toList().get(0);
    }

    public ArticleContent saveArticleContent(ArticleContent content) {
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

    public void deleteContent(Long articleId) {
        contentRepo.remove(ObjectFilters.eq("articleId",articleId))
                .getAffectedCount();
    }

    @Override
    public void destroy() {
        documentDB.commit();
        documentDB.close();
    }
}
