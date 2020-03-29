package org.swdc.note.core.render;

import javafx.stage.FileChooser;
import org.swdc.fx.AppComponent;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleType;

import java.io.File;
import java.nio.file.Path;

public abstract class ContentRender extends AppComponent {

    public abstract String name();

    public abstract String typeName();

    public abstract String renderAsText(Article article);

    public abstract byte[] renderAsBytes(Article article);

    public abstract void renderAsFile(Article article, Path file);

    public abstract void renderAllArticles(ArticleType type, Path file);

    public abstract String subfix();

    public abstract String typeSubfix();

    public boolean support(File file){
        return file.getName().endsWith(subfix());
    }

    public boolean support(Path path) {
        return path.toString().endsWith(subfix());
    }

    public FileChooser.ExtensionFilter getFilters() {
        return new FileChooser.ExtensionFilter(name(), "*." + subfix());
    }

    public FileChooser.ExtensionFilter getTypeFilters(){
        return new FileChooser.ExtensionFilter(typeName(), "*." + typeSubfix());
    }

    @Override
    public String toString() {
        return this.name();
    }
}
