package org.swdc.note.ui.view;

import javafx.scene.control.Label;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;

@View(stage = false,multiple = true,viewLocation = "views/main/MessageView.fxml")
public class MessageView extends AbstractView {

    public void setText(String data) {
        Label label = findById("msgLbl");
        label.setText(data);
    }

}
