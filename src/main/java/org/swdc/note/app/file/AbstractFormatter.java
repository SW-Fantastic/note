package org.swdc.note.app.file;

public abstract class AbstractFormatter<T> implements Formatter<T> {

    public boolean supportExtension(String extension){
        return extension.toLowerCase().trim().equals(getFormatExtension());
    }

    @Override
    public String toString() {
        return this.getFormatName();
    }
}
