package org.swdc.note.core.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Article {

    @Id
    @Getter
    @Setter
    @GeneratedValue
    private Long Id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private Date createDate;

    @Getter
    @Setter
    private String desc;

    @Getter
    @Setter
    @JoinColumn(name = "type_id")
    @ManyToOne(cascade = CascadeType.DETACH)
    private ArticleType type;

    @Getter
    @Setter
    @OneToOne(fetch = FetchType.LAZY,cascade = {CascadeType.REMOVE,CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
    private ArticleContent content;

    @Getter
    @Setter
    @Transient
    private Class contentFormatter;

    @Override
    public String toString() {
        return title;
    }
}
