package org.swdc.note.app.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * 文章分类，树形结构的分类模型
 */
@Entity
public class ArticleType {

    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @Getter
    @Setter
    private ArticleType parentType;

    @OneToMany(mappedBy = "parentType",cascade = CascadeType.REMOVE,fetch = FetchType.EAGER)
    @Getter
    @Setter
    private Set<ArticleType> childType;

    @OneToMany(mappedBy = "type",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<Article> articles;

    @Override
    public String toString() {
        return name;
    }
}
