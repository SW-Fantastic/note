package org.swdc.note.app.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * 文章模型
 */
@Entity
public class Article {

    @Id
    @GeneratedValue
    @Getter
    @Setter
    private Long id;

    /**
     * 标题
     */
    @Getter
    @Setter
    private String title;

    /**
     * 编写时间
     */
    @Getter
    @Setter
    @Column(columnDefinition = "date")
    private Date createdDate;

    /**
     * 分类
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "typeId")
    @Getter
    @Setter
    private ArticleType type;

    /**
     * 内容
     */
    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "contextId")
    @Getter
    @Setter
    private ArticleContext context;

    /**
     * 口令，用BCrypt加密存储
     */
    @Getter
    @Setter
    private String password;

    /**
     * 是否属于加密内容
     * 如果属于，那么展示之前应该进行口令校对
     * 然后用口令解码，使用AES配合口令加密。
     */
    @Getter
    @Setter
    private Boolean secret;

}
