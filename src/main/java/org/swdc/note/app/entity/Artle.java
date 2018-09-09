package org.swdc.note.app.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * 文章模型
 */
@Entity
public class Artle {

    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    @Column(columnDefinition = "date")
    private Date createdDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "typeId")
    @Getter
    @Setter
    private ArtleType type;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "contextId")
    @Getter
    @Setter
    private ArtleContext context;

}
