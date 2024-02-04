package org.swdc.note.ui.component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import org.swdc.fx.font.MaterialIconsService;
import org.swdc.note.ui.component.blocks.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ArticleBlocksEditor extends BorderPane {

    private ListView<ArticleBlock> blockListView;

    private MaterialIconsService iconsService;

    private SimpleBooleanProperty changed = new SimpleBooleanProperty();

    public ArticleBlocksEditor(MaterialIconsService iconsService) {
        this.iconsService = iconsService;
        setPadding(new Insets(12,4,4,4));
        getStyleClass().add("block-editor-view");
        blockListView = new ListView<>();
        blockListView.setCellFactory(c -> new ArticleBlockCell(this,this.iconsService));
        setCenter(blockListView);
    }

    public void addBlock(int index,ArticleBlock block) {
        block.setEditor(this);
        if (index < 0 || index >= blockListView.getItems().size()) {
            blockListView.getItems().add(block);
            blockListView.refresh();
            blockListView.scrollTo(index);
            return;
        }
        blockListView.getItems().add(index,block);
        blockListView.refresh();
        blockListView.scrollTo(index);
    }

    public List<BlockData> getData() {
        List<BlockData> data = new ArrayList<>();
        for (ArticleBlock block: blockListView.getItems()) {
            data.add(block.getData());
        }
        return data;
    }

    public void setData(List<BlockData> data) {
        try {
            blockListView.getItems().clear();
            for (BlockData item : data) {
                Class<ArticleBlock> blockEditorType = (Class<ArticleBlock>) Class
                        .forName(item.getType());
                Constructor<ArticleBlock> ctor = blockEditorType.getConstructor();
                ArticleBlock block = ctor.newInstance();
                block.setEditor(this);
                block.setData(item);
                addBlock(-1,block);
            }
            blockListView.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSource() {
        List<BlockData> data = new ArrayList<>();
        for (ArticleBlock block: blockListView.getItems()) {
            data.add(block.getData());
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setSource(String source) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JavaType type = mapper.getTypeFactory().constructParametricType(List.class,BlockData.class);
            setData(mapper.readValue(source,type));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addList(int index) {
        addBlock(index,new ListBlock());
    }

    public void addHeader(int index) {
        addBlock(index,new HeaderBlock());
    }

    public void addSubHeader(int index) {
        addBlock(index,new SubHeaderBlock());
    }


    public void addTextBlock(int index) {
        addBlock(index, new PlainTextBlock());
    }

    public void addCodeBlock(int index) {
        addBlock(index, new CodeBlock());
    }

    public void addTableBlock(int index) {
        addBlock(index, new TableBlock());
    }

    public void addSpreadBlock(int index) {
        addBlock(index, new SpreadBlock());
    }

    public void addImageBlock(int index) {
        addBlock(index, new ImageBlock());
    }

    public ObservableList<ArticleBlock> getBlocks() {
        return blockListView.getItems();
    }

    public MaterialIconsService getIconsService() {
        return iconsService;
    }

    public SimpleBooleanProperty changedProperty() {
        return changed;
    }

    public boolean isChanged() {
        return changed.get();
    }

    public void setChanged(boolean changed) {
        this.changed.set(changed);
    }
}
