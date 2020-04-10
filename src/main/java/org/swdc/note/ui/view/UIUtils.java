package org.swdc.note.ui.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.swdc.fx.FXView;

public class UIUtils {

    public static MenuItem createMenuItem(String name, EventHandler<ActionEvent> handler, KeyCombination combination) {
        MenuItem menuItem = new MenuItem(name);
        menuItem.setOnAction(handler);
        if (combination != null) {
            menuItem.setAccelerator(combination);
        }
        return menuItem;
    }

    public static void notification(String content, FXView view) {
        MessageView msgView = view.findComponent(MessageView.class);
        msgView.setText(content);
        Notifications.create()
                .graphic(msgView.getView())
                .position(Pos.CENTER)
                .hideAfter(Duration.seconds(2))
                .hideCloseButton()
                .show();
    }

}
