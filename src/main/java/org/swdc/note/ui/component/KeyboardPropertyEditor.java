package org.swdc.note.ui.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.swdc.fx.config.ConfigPropertiesItem;
import org.swdc.fx.config.PropEditorView;
import org.swdc.note.ui.controllers.GlobalKeyListener;
import org.swdc.note.ui.view.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeyboardPropertyEditor extends PropEditorView {

    private HBox hbox;

    private TextField field;

    private List<Integer> codes = new ArrayList<>();

    public KeyboardPropertyEditor(ConfigPropertiesItem item) {
        super(item);
    }

    protected void createEditor() {
        hbox = new HBox();
        hbox.setSpacing(8);
        field = new TextField();
        Button select = new Button();
        select.setText("修改");

        select.setOnAction(e-> {
            field.requestFocus();
            select.setDisable(true);
        });

        HBox.setHgrow(hbox, Priority.ALWAYS);
        hbox.getChildren().addAll(field, select);

        ConfigPropertiesItem item = getItem();
        field.setText(item.getValue() == null ? "" : item.getValue().toString());
        field.setEditable(false);
        field.setOnKeyPressed(e -> {
            if (!select.isDisabled()) {
                return;
            }
            int nCode = UIUtils.getNativeKeyCode(e.getCode());
            codes.add(nCode);
            StringBuilder stringBuilder = new StringBuilder();
            for (int code : codes) {
                if (stringBuilder.isEmpty()) {
                    stringBuilder.append(UIUtils.getKeyCodeFromNative(code).getName());
                } else {
                    stringBuilder.append(" + ").append(UIUtils.getKeyCodeFromNative(code));
                }
            }
            field.setText(stringBuilder.toString());
        });

        field.focusedProperty().addListener(e -> {
            if (!select.isDisabled()) {
                return;
            }
            if (codes.isEmpty()) {
                return;
            }
            getItem().setValue(UIUtils.keyCodeToString(codes.toArray(new Integer[0])));
            codes.clear();
            select.setDisable(false);
        });

        field.setOnKeyReleased(e -> {
            if (!select.isDisabled()) {
                return;
            }
            if (codes.isEmpty()) {
                return;
            }
            getItem().setValue(UIUtils.keyCodeToString(codes.toArray(new Integer[0])));
            codes.clear();
            select.setDisable(false);
        });

    }


    @Override
    public Node getEditor() {
        if (hbox == null) {
            createEditor();
        }
        return hbox;
    }

    @Override
    public Object getValue() {
        if (hbox == null) {
            createEditor();
        }
        return field.getText();
    }

    @Override
    public void setValue(Object o) {
        if (o == null) {
            return;
        }
        if (hbox == null) {
            createEditor();
        }
        String text = o.toString();
        Integer[] codes = UIUtils.stringToKeyCode(text);

        StringBuilder stringBuilder = new StringBuilder();
        for (int code : codes) {
            if (stringBuilder.isEmpty()) {
                stringBuilder.append(UIUtils.getKeyCodeFromNative(code).getName());
            } else {
                stringBuilder.append(" + ").append(UIUtils.getKeyCodeFromNative(code));
            }
        }
        field.setText(stringBuilder.toString());
    }
}
