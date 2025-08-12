package org.swdc.note.core.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import org.dizitart.no2.*;
import org.dizitart.no2.filters.FluentFilter;
import org.dizitart.no2.index.IndexOptions;
import org.dizitart.no2.index.IndexType;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.dizitart.no2.repository.ObjectRepository;
import org.swdc.data.StatelessHelper;
import org.swdc.dependency.annotations.With;
import org.swdc.note.config.AppConfig;
import org.swdc.note.core.aspect.RefreshAspect;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.core.nitire.JacksonMapperModule;

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

        MVStoreModule module = MVStoreModule.withConfig()
                .filePath(new File("./data/articleContent.db"))
                .compressHigh(true)
                .build();

        documentDB = Nitrite.builder()
                .loadModule(module)
                .loadModule(new JacksonMapperModule())
                .openOrCreate();

        contentRepo = documentDB.getRepository(ArticleContent.class);
        if (!contentRepo.hasIndex("id")) {
            contentRepo.createIndex(IndexOptions.indexOptions(IndexType.UNIQUE), "id");
        }
        if (!contentRepo.hasIndex("articleId")) {
            contentRepo.createIndex(IndexOptions.indexOptions(IndexType.NON_UNIQUE),"articleId");
        }
    }


    private int extractVersion(ArticleContent content) {
        return content.getVersion() == null ? 0 : content.getVersion();
    }

    public ArticleContent getArticleContent(String articleId) {
        List<ArticleContent> contents =  contentRepo.find(FluentFilter.where("articleId").eq(articleId))
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
        return contentRepo.find(
                FluentFilter.where("articleId").eq(articleId)
        ).toList();

    }

    public ArticleContent saveArticleContent(ArticleContent content) {

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
        int effect = contentRepo.remove(FluentFilter.where("articleId").eq(articleId))
                .getAffectedCount();
        documentDB.commit();
        return effect;
    }

    public int removeByType(String typeId) {
        int effect = contentRepo
                .remove(FluentFilter.where("typeId").eq(typeId))
                .getAffectedCount();
        documentDB.commit();
        return effect;
    }

    @PreDestroy
    public void destroy() {
        documentDB.commit();
        documentDB.close();
    }
}
