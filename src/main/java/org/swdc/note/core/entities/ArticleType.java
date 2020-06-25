package org.swdc.note.core.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
public class ArticleType {

    @Id
    @Getter
    @Setter
    @GeneratedValue
    private Long id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @OneToMany(mappedBy = "type", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<Article> articles;

    @Getter
    @Setter
    @Transient
    private Class dataProvider;

    @Override
    public String toString() {
        return name;
    }
}
