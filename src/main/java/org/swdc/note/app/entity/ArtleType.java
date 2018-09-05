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
public class ArtleType {

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
    private ArtleType parentType;

    @OneToMany(mappedBy = "parentType")
    @Getter
    @Setter
    private Set<ArtleType> childType;

    @OneToMany(mappedBy = "type",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<Artle> artles;

    @Override
    public String toString() {
        return name;
    }
}
