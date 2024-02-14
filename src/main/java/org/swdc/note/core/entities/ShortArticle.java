package org.swdc.note.core.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class ShortArticle {

    @Id
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column(columnDefinition = "text")
    private String content;

    @ManyToMany(cascade = CascadeType.DETACH)
    private List<ShortArticleTag> tags;

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ShortArticleTag> getTags() {
        return tags;
    }

    public void setTags(List<ShortArticleTag> tags) {
        this.tags = tags;
    }
}
