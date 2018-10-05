package org.swdc.note.app.ui.view.dialogs;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;

/**
 *  表格创建窗口
 *  子谦 - 2018-9-2
 */
@FXMLView("/view/tableDialog.fxml")
public class TableDialog extends AbstractFxmlView {

    @Autowired
    private UIConfig config;

    @Getter
    private Stage stage;

    @Setter
    private Integer rowNum;

    @Setter
    private Integer colNum;

    @PostConstruct
    protected void initUI() throws Exception{
        UIUtil.configTheme((Pane)getView(),config);
        Platform.runLater(()->{
            stage = new Stage();
            Scene sc = new Scene(getView());
            stage.setScene(sc);
            stage.initOwner(GUIState.getStage());
            stage.setResizable(false);
            stage.getIcons().addAll(UIConfig.getImageIcons());
            stage.setTitle("添加表格");
        });
    }

    public String getTable(){
        String table = "";
        if(rowNum!=null && colNum!=null){
            for (int i = 0;i < rowNum + 1; i++){
                for (int j = 0;j < colNum;j++){
                    if(i!=1){
                        table = table + "| <内容> ";
                    }else {
                        table = table + "|:-----:";
                    }
                    if(j+1 == colNum){
                        table = table + "|";
                    }
                }
                table = table + "\n";
            }
        }
        colNum = null;
        rowNum = null;
        return table;
    }

}
