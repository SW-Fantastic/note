package org.swdc.note.app.ui.controller;

import de.felixroske.jfxsupport.GUIState;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.swdc.note.app.event.DeleteEvent;
import org.swdc.note.app.event.ExportEvent;
import org.swdc.note.app.service.ArtleService;
import org.swdc.note.app.service.TypeService;
import org.swdc.note.app.ui.view.dialogs.ExportDialog;

import java.util.Optional;

/**
 * 数据通用控制器
 */
@Controller
public class CommonController {

    @Autowired
    private ArtleService artleService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private ExportDialog exportDialog;

    /**
     * 处理导出事件
     * @param exportEvent 导出事件
     */
    @EventListener
    public void onExportArtle(ExportEvent exportEvent){
        Stage stage = exportDialog.getStage();
        exportDialog.initExport(exportEvent);
        if(stage.isShowing()){
            stage.requestFocus();
        }else{
            stage.show();
        }
    }
    /**
     * 处理文档删除事件
     * @param deleteEvent 删除事件
     */
    @EventListener
    public void onDeleteArtle(DeleteEvent deleteEvent){
        if(deleteEvent.isArtleDel()){
            artleService.deleteArtle(deleteEvent.getArtle());
        }
    }

    /**
     * 处理分类删除事件
     * @param deleteEvent 删除事件
     */
    @EventListener
    public void onTypeDelete(DeleteEvent deleteEvent){
        if(deleteEvent.isArtleTypeDel()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setAlertType(Alert.AlertType.CONFIRMATION);
            alert.setContentText("删除分类，如果分类下含有其他数据，那么也会同时被删除，确定要这样做吗？");
            alert.setTitle("提示");
            alert.initOwner(GUIState.getStage());
            alert.setHeaderText(null);
            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent(btnType->{
                if(btnType.equals(ButtonType.OK)){
                    if(!typeService.delType(deleteEvent.getArtleType(),false)){
                        alert.setAlertType(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("提示");
                        alert.setContentText("此分类下含有其他数据，如果你依然需要删除，那么包括子分类下（如果有子分类的话）的所有数据都将会" +
                                "被删除，依然要这样做吗？");
                        Optional<ButtonType> resultRep = alert.showAndWait();
                        resultRep.ifPresent(btn->{
                            if(btn.equals(ButtonType.OK)){
                                typeService.delType(deleteEvent.getArtleType(),true);
                            }
                        });
                    }
                }
            });
        }
    }

}
