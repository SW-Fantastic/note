package org.swdc.note.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
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
    @Getter
    @Setter
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @JsonIgnore
    @OneToMany(mappedBy = "type", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<Article> articles;

    @Getter
    @Setter
    @Transient
    private Class formatter;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.DETACH)
    private ArticleType parent;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.REMOVE,mappedBy = "parent",fetch = FetchType.EAGER)
    @JsonIgnore
    private List<ArticleType> children;


    @Override
    public String toString() {
        return name;
    }
}
