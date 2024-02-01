package org.swdc.note.ui.component.blocks;

public class BlockData<T> {

    private String type;

    private T content;

    private String source;

    public BlockData() {

    }

    public BlockData(ArticleBlock type, T data) {
        this.content = data;
        this.type = type.getClass().getName();
        this.source = type.generate();
    }

    public String getType() {
        return type;
    }

    public T getContent() {
        return content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(T content) {
        this.content = content;
    }

}
