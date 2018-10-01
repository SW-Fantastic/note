package org.swdc.note.app.ui.controller;

import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.swdc.note.app.event.ArtleDeleteEvent;
import org.swdc.note.app.event.ExportEvent;
import org.swdc.note.app.service.ArtleService;
import org.swdc.note.app.ui.view.dialogs.ExportDialog;

/**
 * 处理列表的各种事件。
 */
@Controller
public class ArtleCellController {

    @Autowired
    private ArtleService artleService;

    @Autowired
    private ExportDialog exportDialog;

    /**
     * 用户在列表中点击了删除
     * @param deleteEvent 删除事件
     */
    @EventListener
    public void onDeleteArtle(ArtleDeleteEvent deleteEvent){
        artleService.deleteArtle(deleteEvent.getArtle());
    }

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


}
