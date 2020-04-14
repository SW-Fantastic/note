package org.swdc.note.core.entities;

import lombok.Data;

import java.util.Map;

@Data
public class ArticleResource {

    private Map<String, byte[]> images;

}
