package org.swdc.note.core.service;


import jakarta.inject.Inject;
import org.swdc.data.StatelessHelper;
import org.swdc.data.anno.Transactional;
import org.swdc.dependency.annotations.With;
import org.swdc.note.core.aspect.RefreshAspect;
import org.swdc.note.core.entities.ArticleType;
import org.swdc.note.core.entities.CollectionArticle;
import org.swdc.note.core.entities.CollectionType;
import org.swdc.note.core.repo.CollectionRepo;
import org.swdc.note.core.repo.CollectionTypeRepo;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@With(aspectBy = RefreshAspect.class)
public class CollectionService {

    @Inject
    private CollectionTypeRepo typeRepo;

    @Inject
    private CollectionRepo collectionRepo;

    @Transactional
    public CollectionType saveType(CollectionType type) {
        if(type == null || type.getTitle() == null || type.getTitle().isBlank()) {
            return null;
        }

        CollectionType parent = type.getParent();
        if (parent != null) {
            parent = typeRepo.getOne(parent.getId());
        }
        type.setParent(parent);
        if (type.getId() == null || type.getId().isBlank()) {

            List<CollectionType> exists = null;
            if (parent != null) {
                exists = typeRepo.findByTypeTitleAndParent(type.getTitle(),parent.getId());
            } else {
                exists = typeRepo.findByTypeTitle(type.getTitle());
            }

            if (exists == null || exists.isEmpty()) {
                return typeRepo.save(type);
            }

            return exists.get(0);
        } else {
            return typeRepo.save(type);
        }
    }

    @Transactional
    public List<CollectionType> getCollectionTypes() {

        List<CollectionType> types = typeRepo.findRootTypes();
        if (types == null) {
            return Collections.emptyList();
        }

        return types.stream().filter(Objects::nonNull)
                .map(StatelessHelper::stateless)
                .toList();
    }


    public CollectionType getType(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        CollectionType type = typeRepo.getOne(id);
        return StatelessHelper
                .stateless(type);
    }

    @Transactional
    public List<CollectionArticle> getArticles(String typeId) {
        if (typeId == null || typeId.isBlank()) {
            return Collections.emptyList();
        }
        List<CollectionArticle> articles = collectionRepo.findByType(typeId);
        return articles.stream()
                .filter(Objects::nonNull)
                .map(StatelessHelper::stateless)
                .toList();
    }

    @Transactional
    public CollectionArticle saveCollection(CollectionArticle article) {
        if (article == null || article.getTitle() == null || article.getTitle().isBlank()) {
            return null;
        }
        if (article.getType() == null) {
            return null;
        }
        CollectionArticle target = null;
        CollectionType type = typeRepo.getOne(article.getType().getId());
        if (article.getId() != null && !article.getId().isBlank()) {
            target = collectionRepo.getOne(article.getId());
        } else {
            target = new CollectionArticle();
            target.setCreatedAt(new Date());
        }
        target.setType(type);
        target.setTitle(article.getTitle());
        target = collectionRepo.save(target);
        return StatelessHelper.stateless(target);
    }

}
