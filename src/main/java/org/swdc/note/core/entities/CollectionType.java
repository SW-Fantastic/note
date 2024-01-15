package org.swdc.note.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class CollectionType {

    @Id
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private String id;

    @Column(columnDefinition = "text")
    private String title;

    private Date date;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE,mappedBy = "parent")
    private List<CollectionType> children;

    @ManyToOne(cascade = CascadeType.DETACH)
    private CollectionType parent;

    @OneToMany(mappedBy = "type", cascade = CascadeType.REMOVE)
    private List<CollectionArticle> articles;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<CollectionType> getChildren() {
        return children;
    }

    public void setChildren(List<CollectionType> children) {
        this.children = children;
    }

    public CollectionType getParent() {
        return parent;
    }

    public void setParent(CollectionType parent) {
        this.parent = parent;
    }

    public List<CollectionArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<CollectionArticle> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return title;
    }
}
