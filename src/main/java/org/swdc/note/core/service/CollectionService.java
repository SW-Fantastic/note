package org.swdc.note.core.service;


import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.swdc.data.StatelessHelper;
import org.swdc.data.anno.Transactional;
import org.swdc.dependency.annotations.With;
import org.swdc.fx.FXResources;
import org.swdc.note.core.aspect.RefreshAspect;
import org.swdc.note.core.entities.CollectionArticle;
import org.swdc.note.core.entities.CollectionFocus;
import org.swdc.note.core.entities.CollectionType;
import org.swdc.note.core.repo.CollectionFocusRepo;
import org.swdc.note.core.repo.CollectionRepo;
import org.swdc.note.core.repo.CollectionTypeRepo;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@With(aspectBy = RefreshAspect.class)
public class CollectionService {

    @Inject
    private CollectionTypeRepo typeRepo;

    @Inject
    private CollectionFocusRepo focusRepo;

    @Inject
    private CollectionRepo collectionRepo;

    @Inject
    private FXResources resources;

    @Inject
    private Logger logger;

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
                return StatelessHelper.stateless(
                        typeRepo.save(type)
                );
            }

            return exists.get(0);
        } else {
            return StatelessHelper.stateless(
                    typeRepo.save(type)
            );
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
    public CollectionType getTypeByHost(String host) {
        if (host == null || host.isBlank()) {
            return null;
        }
        CollectionType target = typeRepo.findByHost(host);
        if (target == null) {
            return null;
        }
        return StatelessHelper
                .stateless(target);
    }

    public CollectionArticle getArticleById(String articleId) {
        if (articleId == null ||articleId.isBlank()) {
            return null;
        }
        CollectionArticle article = collectionRepo.getOne(articleId);
        if (article != null) {
            return StatelessHelper
                    .stateless(article);
        }
        return null;
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

    @Transactional
    public void deleteCollectionArticle(CollectionArticle article) {

        if (article == null || article.getId().isBlank()) {
            return;
        }

        CollectionArticle theArticle = collectionRepo.getOne(article.getId());
        collectionRepo.remove(theArticle);
        File assetRoot = resources.getAssetsFolder();
        File collectionsRoot = new File(assetRoot.getAbsolutePath() + File.separator + "collections");
        if (!collectionsRoot.exists()) {
            return;
        }
        try {
            File documentFile = new File(collectionsRoot.getAbsolutePath() + File.separator + theArticle.getId());
            Files.delete(documentFile.toPath());
        } catch (Exception e) {
            logger.error("Failed to remove a file: " + theArticle.getId(), e);
        }

    }

    @Transactional
    public void deleteCollectionType(CollectionType collectionType) {

        if (collectionType == null || collectionType.getId() == null || collectionType.getId().isBlank()) {
            return;
        }

        CollectionType theType = typeRepo.getOne(collectionType.getId());
        List<CollectionType> subTypes = theType.getChildren();
        for (CollectionType type: subTypes) {
            deleteCollectionType(type);
        }
        List<CollectionArticle> articles = theType.getArticles();
        for (CollectionArticle article: articles) {
            deleteCollectionArticle(article);
        }

        typeRepo.remove(theType);

    }

    @Transactional
    public List<CollectionFocus> getFocuses() {
        List<CollectionFocus> focusList = focusRepo.getAll();
        if (focusList == null) {
            return Collections.emptyList();
        }
        return focusList.stream()
                .map(StatelessHelper::stateless)
                .collect(Collectors.toList());
    }

    @Transactional
    public CollectionFocus getFocus(String host) {
        if (host == null || host.isBlank()) {
            return null;
        }
        CollectionFocus focus = focusRepo.getCollectionFocusByMatch(host);
        if (focus != null) {
            return StatelessHelper
                    .stateless(focus);
        }
        return null;
    }

    public CollectionFocus findFocus(String uri) {
        if (uri == null || uri.isBlank()) {
            return null;
        }
        try {
            URL url = new URI(uri).toURL();
            List<CollectionFocus> focusList = focusRepo.getCollectionFocusByHost(url.getHost());
            CollectionFocus focus = null;
            for (CollectionFocus item: focusList) {
                if (item.isMatched(uri) ) {
                    if (focus != null && item.getUrlMatch().length() > focus.getUrlMatch().length()) {
                        focus = item;
                    } else if (focus == null) {
                        focus = item;
                    }
                }
            }
            return focus;
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public CollectionFocus saveFocus(String host,String match,String selector) {
        if (match == null || match.isBlank() || selector == null ||selector.isBlank()) {
            return null;
        }
        CollectionFocus focus = focusRepo.getCollectionFocusByMatch(match);
        if (focus == null) {
            focus = new CollectionFocus();
            focus.setHost(host);
        }
        focus.setUrlMatch(match);
        focus.setSelector(selector);
        focus = focusRepo.save(focus);
        return StatelessHelper
                .stateless(focus);
    }

    public void removeFocus(CollectionFocus focus) {
        if (focus == null || focus.getId() == null) {
            return;
        }
        CollectionFocus target = focusRepo.getOne(focus.getId());
        focusRepo.remove(target);
    }

}
