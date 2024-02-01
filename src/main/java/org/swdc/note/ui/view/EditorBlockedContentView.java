package org.swdc.note.ui.view;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.scene.layout.BorderPane;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.note.ui.component.ArticleBlocksEditor;

@View(stage = false,viewLocation = "views/main/EditorBlockedContentView.fxml",multiple = true)
public class EditorBlockedContentView extends AbstractView {

    private ArticleBlocksEditor blocksEditor;

    @Inject
    private MaterialIconsService iconsService;

    @PostConstruct
    public void init() {
        blocksEditor = new ArticleBlocksEditor(iconsService);
        BorderPane pane = findById("editorPane");
        pane.setCenter(blocksEditor);

        blocksEditor.addTextBlock(0);
    }

    public void setSource(String text) {
        blocksEditor.setSource(text);
    }
    public String getSource() {
        return blocksEditor.getSource();
    }

}
