package org.swdc.note.app.repository;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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

    @ManyToOne
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
