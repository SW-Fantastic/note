package org.swdc.note.app.repository;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 *  文章内容模型
 */
@Entity
public class ArtleContext {

    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String content;

    @OneToOne
    @JoinColumn(name = "artleId")
    @Getter
    @Setter
    private Artle artle;

}
