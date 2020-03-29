package org.swdc.note.ui.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;

public class UIUtils {

    public static MenuItem createMenuItem(String name, EventHandler<ActionEvent> handler, KeyCombination combination) {
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(handler);
        if (combination != null) {
            menuItem.setAccelerator(combination);
        }
        return menuItem;
    }

}
