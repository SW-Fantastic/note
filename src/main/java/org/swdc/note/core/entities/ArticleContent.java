package org.swdc.note.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class ArticleContent {

    @Id
    @Getter
    @Setter
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "blob")
    private byte[] resources;

    @Getter
    @Setter
    @Column(columnDefinition = "text")
    private String source;

    @Getter
    @Setter
    @JsonIgnore
    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH,CascadeType.REMOVE},mappedBy = "content")
    private Article article;

    public void setResources(ArticleResource resources) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.resources = mapper.writeValueAsBytes(resources);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ArticleResource getResources() {
        try {
            if (resources == null) {
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(this.resources, ArticleResource.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
