package org.swdc.note.core.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(indexes = {@Index(name = "typeIndex", columnList = "type_id")})
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
    @Transient
    private Class contentFormatter;

    @Getter
    @Setter
    @Transient
    private String location;

    @Getter
    @Setter
    @Transient
    private ArticleContent content;

    @Override
    public String toString() {
        return title;
    }
}
