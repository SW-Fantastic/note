package org.swdc.note.core.entities;


import jakarta.persistence.*;
import java.util.List;

@Entity
public class ShortArticleTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "tags",cascade = CascadeType.DETACH)
    private List<ShortArticle> articles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ShortArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<ShortArticle> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return name;
    }
}
