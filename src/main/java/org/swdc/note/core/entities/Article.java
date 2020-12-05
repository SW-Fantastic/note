package org.swdc.note.core.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(indexes = {@Index(name = "typeIndex", columnList = "type_id")})
public class Article {

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
    private String Id;

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
