package org.swdc.note.ui.view.cells;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.note.core.entities.Article;

public class ArticleCellDragData {

    private String articleId;

    private String articleTypeId;

    private static final Logger logger = LoggerFactory.getLogger(ArticleCellDragData.class);

    public ArticleCellDragData() {

    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getArticleTypeId() {
        return articleTypeId;
    }

    public void setArticleTypeId(String articleTypeId) {
        this.articleTypeId = articleTypeId;
    }

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
