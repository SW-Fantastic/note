package org.swdc.note.ui.view.cells;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.note.core.entities.Article;

@NoArgsConstructor
public class ArticleCellDragData {

    @Getter
    @Setter
    private String articleId;

    @Getter
    @Setter
    private String articleTypeId;

    private static final Logger logger = LoggerFactory.getLogger(ArticleCellDragData.class);

    public ArticleCellDragData(Article article) {
        this.articleId = article.getId();
        this.articleTypeId = article.getType().getId();
    }

    public String asString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            logger.error("fail to process data on drag start", e);
            return null;
        }
    }

    public static ArticleCellDragData fromData(String data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(data, ArticleCellDragData.class);
        } catch (Exception ex){
            logger.error("fail to process data on drop", ex);
            return null;
        }
    }

}
