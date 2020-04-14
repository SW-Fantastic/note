package org.swdc.note.ui.view;

import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import lombok.Getter;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Scope;
import org.swdc.fx.anno.ScopeType;
import org.swdc.fx.anno.View;
import org.swdc.note.ui.view.dialogs.ImagesView;

import java.util.List;
import java.util.Optional;

import static org.swdc.note.ui.view.UIUtils.createMenuItem;

@Scope(ScopeType.MULTI)
@View(stage = false)
public class EditorContentView extends FXView {

    @Getter
    private CodeArea codeArea;

    @Aware
    @Getter
    private ImagesView imagesView = null;

    private boolean hasSaved = false;

    public void setSaved() {
        hasSaved = true;
    }

    public void setChanged() {
        hasSaved = false;
    }

    public boolean hasSaved() {
        return hasSaved;
    }

    private ContextMenu editorMenu = null;

    /**
     * 如果inputRequest为null，那么MAC系统将会出现无法输入中文的问题。
     */
    private static class InputMethodRequestsObject implements InputMethodRequests {

        private CodeArea area;

        InputMethodRequestsObject(CodeArea area){
            this.area = area;
        }

        @Override
        public Point2D getTextLocation(int offset) {
            Optional<Bounds> bounds = area.getCaretBounds();
            if (bounds.isPresent()){
                Bounds pos = bounds.get();
                return new Point2D(pos.getMinX(),pos.getMinY());
            }
            return new Point2D(0,0);
        }

        @Override
        public int getLocationOffset(int x, int y) {
            return 0;
        }

        @Override
        public void cancelLatestCommittedText() {

        }

        @Override
        public String getSelectedText() {
            return area.getSelectedText();
        }
    }

    @Override
    public void initialize() {
        BorderPane borderPane = findById("editor");
        CodeArea codeArea = new CodeArea();
        codeArea.setInputMethodRequests(new InputMethodRequestsObject(codeArea));
        codeArea.setOnInputMethodTextChanged(e->{
            if(e.getCommitted() != null){
                codeArea.insertText(codeArea.getCaretPosition(),e.getCommitted());
            }
        });
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.getStyleClass().add("code-area");

        borderPane.setCenter(new VirtualizedScrollPane<>(codeArea));
        this.codeArea = codeArea;
        this.createEditorMenu();
        this.codeArea.setContextMenu(this.editorMenu);
    }

    private void createEditorMenu() {
        this.editorMenu = new ContextMenu();
        List<MenuItem> menuItems = this.editorMenu.getItems();
        MenuItem undoItem = createMenuItem("撤销   ",this::onUndo,new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        menuItems.add(undoItem);

        MenuItem redoItem = createMenuItem("重做   ", this::onRedo, new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        menuItems.add(redoItem);
        codeArea.textProperty().addListener(e -> {
            if (codeArea.getUndoManager().isUndoAvailable()) {
                undoItem.setDisable(false);
            } else {
                undoItem.setDisable(true);
            }
            if (codeArea.getUndoManager().isRedoAvailable()) {
                redoItem.setDisable(false);
            } else {
                redoItem.setDisable(true);
            }
        });

        menuItems.add(new SeparatorMenuItem());
        menuItems.add(createMenuItem("剪切   ",this::onCut,new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN)));
        menuItems.add(createMenuItem("复制   ", this::onCopy, new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN)));
        menuItems.add(createMenuItem("粘贴   ", this::onPaste, new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN)));
        menuItems.add(createMenuItem("删除   ", this::onDelete, new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN)));
        menuItems.add(new SeparatorMenuItem());
        menuItems.add(createMenuItem("全选   ",this::onSelectAll,new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN)));
    }

    private void onCopy(ActionEvent event) {
       codeArea.copy();
    }

    private void onCut(ActionEvent event) {
        codeArea.cut();
    }

    private void onPaste(ActionEvent event) {
        codeArea.paste();
    }

    private void onUndo(ActionEvent event) {
        codeArea.undo();
    }

    private void onRedo(ActionEvent event) {
        codeArea.redo();
    }

    private void onDelete(ActionEvent event) {
        IndexRange range = codeArea.getSelection();
        if (range == null) {
            return;
        }
        codeArea.deleteText(range);
    }

    private void onSelectAll(ActionEvent event) {
        codeArea.selectAll();
    }

    public WebView getWebView() {
        return findById("wView");
    }

}
