package org.swdc.note.core.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import org.dizitart.no2.*;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.swdc.data.StatelessHelper;
import org.swdc.dependency.annotations.With;
import org.swdc.note.config.AppConfig;
import org.swdc.note.core.aspect.RefreshAspect;
import org.swdc.note.core.entities.ArticleContent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@With(aspectBy = RefreshAspect.class)
public class ContentService  {

    private Nitrite documentDB;

    private ObjectRepository<ArticleContent> contentRepo;

    @Inject
    private AppConfig appConfig;


    @PostConstruct
    public void initialize() {
        documentDB = Nitrite.builder()
                .compressed()
                .filePath(new File("./data/articleContent.db"))
                .openOrCreate();


        contentRepo = documentDB.getRepository(ArticleContent.class);
        if (!contentRepo.hasIndex("id")) {
            contentRepo.createIndex("id", IndexOptions.indexOptions(IndexType.Unique));
        }
        if (!contentRepo.hasIndex("articleId")) {
            contentRepo.createIndex("articleId",IndexOptions.indexOptions(IndexType.NonUnique));
        }
    }


    private int extractVersion(ArticleContent content) {
        return content.getVersion() == null ? 0 : content.getVersion();
    }

    public ArticleContent getArticleContent(String articleId) {
        List<ArticleContent> contents =  contentRepo.find(ObjectFilters.eq("articleId",articleId))
                .toList();
        if (contents.size() > 0) {
            return contents.stream()
                    .sorted((dA,dB) -> extractVersion(dB) - extractVersion(dA))
                    .findFirst()
                    .get();
        } else {
            return null;
        }
    }

    public List<ArticleContent> getVersions(String articleId) {
        return contentRepo.find(ObjectFilters.and(
                ObjectFilters.eq("articleId",articleId)
        )).toList();

    }

    public ArticleContent saveArticleContent(ArticleContent content) {

        documentDB.compact();

        String uuid = UUID.randomUUID().toString();
        if (content.getId() == null) {
            content.setId(uuid);
        }

        if (content.getArticleId() == null) {
            return null;
        }

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(date);

        if (getArticleContent(content.getArticleId()) == null) {
            content.setUpdateDate(today);
            if (appConfig.getVersionsControl()) {
                content.setVersion(0);
            }
            int effect = contentRepo.insert(content).getAffectedCount();
            if (effect > 0) {
                return content;
            }
            return null;
        } else {
            if (appConfig.getVersionsControl()) {
                uuid = UUID.randomUUID().toString();
                ArticleContent newVersion = new ArticleContent();
                newVersion.setUpdateDate(today);
                newVersion.setVersion(extractVersion(content) + 1);
                newVersion.setImages(content.getImages());
                newVersion.setSource(content.getSource());
                newVersion.setTypeId(content.getTypeId());
                newVersion.setArticleId(content.getArticleId());
                newVersion.setId(uuid);
                newVersion.setUpdateDate(today);
                int effect = contentRepo.insert(newVersion).getAffectedCount();
                if (effect > 0) {
                    return content;
                }
            } else {
                content.setUpdateDate(today);
                int effect = contentRepo.update(content).getAffectedCount();
                if (effect > 0) {
                    return content;
                }
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

    @PreDestroy
    public void destroy() {
        documentDB.commit();
        documentDB.close();
    }
}
