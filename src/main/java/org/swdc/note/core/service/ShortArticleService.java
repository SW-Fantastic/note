package org.swdc.note.core.service;

import jakarta.inject.Inject;
import org.swdc.data.StatelessHelper;
import org.swdc.data.anno.Transactional;
import org.swdc.note.core.entities.ShortArticle;
import org.swdc.note.core.entities.ShortArticleTag;
import org.swdc.note.core.repo.ShortArticleRepo;
import org.swdc.note.core.repo.ShortArticleTagRepo;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ShortArticleService {

    @Inject
    private ShortArticleTagRepo tagRepo;

    @Inject
    private ShortArticleRepo shortArticleRepo;

    @Transactional
    public ShortArticleTag createTag(String tag) {

        if (tag == null || tag.isBlank()) {
            return null;
        }

        ShortArticleTag exists = tagRepo.findByName(tag);
        if (exists == null) {
            exists = new ShortArticleTag();
            exists.setName(tag);
            exists = tagRepo.save(exists);
        }

        return StatelessHelper
                .stateless(exists);
    }

    @Transactional
    public List<ShortArticleTag> getTags() {

        List<ShortArticleTag> tags = tagRepo.getAll();
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }

        return tags.stream()
                .map(StatelessHelper::stateless)
                .collect(Collectors.toList());

    }

    @Transactional
    public List<ShortArticleTag> searchTagsBy(String name) {

        List<ShortArticleTag> tags = tagRepo.searchByName(name);
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }
        return tags.stream()
                .map(StatelessHelper::stateless)
                .toList();

    }

    @Transactional
    public boolean removeTag(Long id) {

        if (id == null || id < 0) {
            return false;
        }

        ShortArticleTag tag = tagRepo.getOne(id);
        if (tag == null) {
            return false;
        }
        List<ShortArticle> articles = tag.getArticles();
        for (ShortArticle article : articles) {
            if (article.getTags().size() == 1) {
                shortArticleRepo.remove(article);
            }
        }
        tagRepo.remove(tag);
        return true;

    }

    @Transactional
    public ShortArticle createArticle(String content, List<ShortArticleTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        tags = tags.stream()
                .filter(Objects::nonNull)
                .map(ShortArticleTag::getId)
                .map(tagRepo::getOne)
                .toList();

        ShortArticle article = new ShortArticle();
        article.setContent(content);
        article.setDate(new Date());
        article.setTags(tags);

        article = shortArticleRepo.save(article);
        if (article == null) {
            return null;
        }

        return StatelessHelper.stateless(article);
    }

    @Transactional
    public List<ShortArticle> getArticles(Long tagId) {
        if (tagId == null || tagId < 0) {
            return Collections.emptyList();
        }
        ShortArticleTag articleTag = tagRepo.getOne(tagId);
        if (articleTag == null || articleTag.getArticles().isEmpty()) {
            return Collections.emptyList();
        }
        List<ShortArticle> articles = articleTag.getArticles();
        return articles.stream()
                .filter(Objects::nonNull)
                .map(StatelessHelper::stateless)
                .toList();
    }

}
