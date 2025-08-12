package org.swdc.note.core.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;

@Entity
public class CollectionArticle {

    @Id
    @UuidGenerator
    @GeneratedValue(generator = "uuid")
    private String id;

    private String title;

    @Column(columnDefinition = "timestamp")
    private Date createdAt;

    @Column(columnDefinition = "text")
    private String source;

    @ManyToOne(cascade = CascadeType.DETACH)
    private CollectionType type;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public CollectionType getType() {
        return type;
    }

    public void setType(CollectionType type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
