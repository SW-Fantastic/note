package org.swdc.note.ui.view;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.beans.property.BooleanProperty;
import javafx.scene.layout.BorderPane;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.ui.component.ArticleBlocksEditor;
import org.swdc.note.ui.component.blocks.BlockData;

import java.util.List;

@View(stage = false,viewLocation = "views/main/EditorBlockedContentView.fxml",multiple = true)
public class EditorBlockedContentView extends AbstractView {

    private ArticleBlocksEditor blocksEditor;

    private BooleanProperty changed;

    @Inject
    private MaterialIconsService iconsService;

    @PostConstruct
    public void init() {
        blocksEditor = new ArticleBlocksEditor(iconsService);
        BorderPane pane = findById("editorPane");
        pane.setCenter(blocksEditor);

        blocksEditor.addTextBlock(0);
        this.changed = blocksEditor.changedProperty();
    }

    public void setSource(List<BlockData> data) {
        blocksEditor.setData(data);
    }
    public List<BlockData> getSource() {
        return blocksEditor.getData();
    }

    public BooleanProperty changedProperty() {
        return changed;
    }

    public boolean isChanged() {
        return changed.get();
    }

    public void setChanged(boolean changed) {
        this.changed.set(changed);
    }
}
