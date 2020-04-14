package org.swdc.note.ui.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.property.editor.PropertyEditor;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.swdc.fx.AppComponent;
import org.swdc.fx.anno.ConfigProperty;
import org.swdc.fx.properties.AbstractPropEditor;
import org.swdc.note.ui.controllers.GlobalKeyListener;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeyboardPropertyEditor extends AbstractPropEditor {

    public KeyboardPropertyEditor(ConfigProperty prop, AppComponent component) {
        super(prop, component);
    }

    @Override
    protected PropertyEditor<String> createEditor() {
        HBox hbox = new HBox();
        hbox.setSpacing(8);
        TextField field = new TextField();
        Button select = new Button();
        select.setText("修改");
        ObjectProperty<Integer[]> data = new SimpleObjectProperty<>();
        GlobalKeyListener listener = getParent().findComponent(GlobalKeyListener.class);
        listener.bindPressedKeys(data);

        select.setOnAction(e-> {
            select.setDisable(true);
            listener.enable(data,true);
        });

        HBox.setHgrow(hbox, Priority.ALWAYS);
        hbox.getChildren().addAll(field, select);

        field.setText(getProp().getPropData().value());
        field.setEditable(false);

        String original = getProp().getValue().toString();
        Integer[] codeData = stringToKeyCode(original);

        data.addListener(((observableValue, integers, newVal) -> {
            if (integers == null || newVal == null) {
                return;
            }
            String text = "";
            for (Integer code: integers) {
                text = text + NativeKeyEvent.getKeyText(code) + " ";
            }
            field.setText(text);
            listener.enable(data,false);
            String propData = keyCodeToString(data.getValue());
            getProp().setValue(propData);
            select.setDisable(false);
        }));
        data.setValue(codeData);
        return new PropertyEditor<>() {
            @Override
            public Node getEditor() {
                return hbox;
            }

            @Override
            public String getValue() {
                return keyCodeToString(data.getValue());
            }

            @Override
            public void setValue(String o) {
                if (o == null){
                    return;
                }
                Integer[] codeData = stringToKeyCode(o);
                data.setValue(codeData);
            }
        };
    }

    public static String keyCodeToString(Integer[] codes) {
        return Stream.of(codes)
                .map(cd->cd + "")
                .reduce((cdA, cdB) -> cdA + "," + cdB)
                .orElse("");
    }

    public static Integer[] stringToKeyCode(String data) {
        return Stream.of(data.split(","))
                .map(Integer::valueOf)
                .collect(Collectors.toList())
                .toArray(new Integer[0]);
    }

}
