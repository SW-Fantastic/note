package org.swdc.note.core.entities;

import lombok.Getter;
import lombok.Setter;
import org.dizitart.no2.objects.Id;

import java.util.Map;

public class ArticleContent {

    @Id
    @Getter
    @Setter
    private Long articleId;

    @Getter
    @Setter
    private Map<String, byte[]> images;

    @Getter
    @Setter
    private String source;

}
