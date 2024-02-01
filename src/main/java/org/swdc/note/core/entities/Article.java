package org.swdc.note.core.entities;

import org.hibernate.annotations.GenericGenerator;
import org.swdc.note.core.files.SingleStorage;

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
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid")
    private String Id;

    private String title;

    private Date createDate;

    private String desc;

    @JoinColumn(name = "type_id")
    @ManyToOne(cascade = CascadeType.DETACH,fetch = FetchType.EAGER)
    private ArticleType type;

    /**
     * 单独存储的处理器。
     * 如果此对象直接来自文件的话，那么这里指定打开它
     * 所使用的SingleStore的class。
     * 不存入数据库。
     */
    @Transient
    private Class<? extends SingleStorage> singleStore;

    /**
     * 全路径。
     * 如果此对象直接来自文件，这里记载文件的全路径。
     * 不存入数据库。
     */
    @Transient
    private String fullPath;


    @Transient
    private ArticleContent content;

    @Enumerated(EnumType.STRING)
    private ArticleEditorType editorType;

    @Override
    public String toString() {
        return title;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ArticleType getType() {
        return type;
    }

    public void setType(ArticleType type) {
        this.type = type;
    }

    public Class<? extends SingleStorage> getSingleStore() {
        return singleStore;
    }

    public void setSingleStore(Class<? extends SingleStorage> singleStore) {
        this.singleStore = singleStore;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public ArticleContent getContent() {
        return content;
    }

    public void setContent(ArticleContent content) {
        this.content = content;
    }

    public ArticleEditorType getEditorType() {
        return editorType;
    }

    public void setEditorType(ArticleEditorType editorType) {
        this.editorType = editorType;
    }
}
