package org.swdc.note.app.ui.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;

/**
 * 用于显示提示消息的视图
 */
@FXMLView("/view/messageView.fxml")
public class MessageView extends AbstractFxmlView {

    @Autowired
    private UIConfig config;

    @PostConstruct
    protected void initUI () throws Exception {
        UIUtil.configTheme((BorderPane)this.getView(), config);
    }

    public void setMessage(String message) {
        Label lblMessage = (Label)this.getView().lookup("#msgLbl");
        lblMessage.setText(message);
    }

}
