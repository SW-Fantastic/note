package org.swdc.note.core.entities;

import java.util.Map;

public class ArticleResource {

    private Map<String, byte[]> images;

    public void setImages(Map<String, byte[]> images) {
        this.images = images;
    }

    public Map<String, byte[]> getImages() {
        return images;
    }
}
