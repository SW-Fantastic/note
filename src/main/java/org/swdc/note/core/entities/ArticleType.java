package org.swdc.note.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class ArticleType {

    /**
     * 这里使用的是UUID，
     * 和数字型自增的id不同，他是通过算法生成的字符串型id
     * 基于随机量和时间，所以可以直接导出到外部数据文件中，
     * 或者在NoSQL上面使用。
     */
    @Id
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private String id;

    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "type", cascade = CascadeType.REMOVE)
    private Set<Article> articles;

    @Transient
    private Class formatter;

    @ManyToOne(cascade = CascadeType.DETACH)
    private ArticleType parent;

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy = "parent")
    @JsonIgnore
    private List<ArticleType> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Article> getArticles() {
        return articles;
    }

    public void setArticles(Set<Article> articles) {
        this.articles = articles;
    }

    public Class getFormatter() {
        return formatter;
    }

    public void setFormatter(Class formatter) {
        this.formatter = formatter;
    }

    public ArticleType getParent() {
        return parent;
    }

    public void setParent(ArticleType parent) {
        this.parent = parent;
    }

    public List<ArticleType> getChildren() {
        return children;
    }

    public void setChildren(List<ArticleType> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return name;
    }
}
