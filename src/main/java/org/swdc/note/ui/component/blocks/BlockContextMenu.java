package org.swdc.note.ui.component.blocks;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.swdc.note.ui.component.ArticleBlocksEditor;

public class BlockContextMenu extends ContextMenu {

    private int currentIndex;

    private ArticleBlocksEditor editor;

    private SimpleBooleanProperty unremovable = new SimpleBooleanProperty();

    public BlockContextMenu(ArticleBlocksEditor editor) {

        this.editor = editor;

        MenuItem itemTrash = new MenuItem("删除");
        itemTrash.disableProperty().bind(unremovable);
        itemTrash.setOnAction(e -> {
            if (editor.getBlocks().size() == 1) {
                return;
            }
            editor.getBlocks().remove(currentIndex);
        });

        MenuItem itemText = new MenuItem("段落");
        itemText.setOnAction(e -> {
            editor.addTextBlock(currentIndex + 1);
        });

        MenuItem itemHeader = new MenuItem("标题");
        itemHeader.setOnAction(e -> {
            editor.addHeader(currentIndex + 1);
        });

        MenuItem itemSubHeader = new MenuItem("副标题");
        itemSubHeader.setOnAction(e -> {
            editor.addSubHeader(currentIndex + 1);
        });

        MenuItem itemList = new MenuItem("列表");
        itemList.setOnAction(e -> {
            editor.addList(currentIndex + 1);
        });

        MenuItem itemCodeBlock = new MenuItem("代码块");
        itemCodeBlock.setOnAction(e -> {
            editor.addCodeBlock(currentIndex + 1);
        });

        MenuItem itemTableBlock = new MenuItem("表格");
        itemTableBlock.setOnAction(e -> {
            editor.addTableBlock(currentIndex + 1);
        });

        MenuItem itemSpread = new MenuItem("分隔符");
        itemSpread.setOnAction(e -> {
            editor.addSpreadBlock(currentIndex + 1);
        });

        MenuItem itemImage = new MenuItem("图片");
        itemImage.setOnAction(e -> {
            editor.addImageBlock(currentIndex + 1);
        });

        MenuItem itemRef =  new MenuItem("引用");
        itemRef.setOnAction(e -> {
            editor.addReferenceBlock(currentIndex + 1);
        });

        getItems().addAll(
                itemTrash,
                new SeparatorMenuItem(),
                itemText,
                itemHeader,
                itemSubHeader,
                itemList,
                itemCodeBlock,
                itemTableBlock,
                itemSpread,
                itemImage,
                itemRef
        );
    }

    public void show(int index, Node anchor) {
        unremovable.setValue(editor.getBlocks().size() == 1);
        this.currentIndex = index;
        this.show(anchor, Side.BOTTOM,0,0);
    }

}
