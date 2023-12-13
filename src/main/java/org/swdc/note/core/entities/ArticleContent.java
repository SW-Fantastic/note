package org.swdc.note.core.entities;

import org.dizitart.no2.objects.Id;

import java.util.Map;

public class ArticleContent {

    @Id
    private String id;

    private String articleId;

    private String typeId;

    private Map<String, byte[]> images;

    private String source;

    private Integer version;

    private String updateDate;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public Map<String, byte[]> getImages() {
        return images;
    }

    public void setImages(Map<String, byte[]> images) {
        this.images = images;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
