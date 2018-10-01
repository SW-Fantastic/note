package org.swdc.note.app.ui.controller;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.Initializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.entity.ArtleContext;
import org.swdc.note.app.event.ArtleDeleteEvent;
import org.swdc.note.app.event.ArtleOpenEvent;
import org.swdc.note.app.service.ArtleService;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.StartReadView;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 阅读视图的控制器。
 */
@FXMLController
public class ReadViewController implements Initializable {

    @Autowired
    private StartReadView readView;

    @Autowired
    private UIConfig config;

    @Autowired
    private ArtleService artleService;

    @Autowired
    private Parser parser;

    @Autowired
    private HtmlRenderer renderer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @EventListener
    public void onArtleOpen(ArtleOpenEvent e){
        Artle artle = e.getArtle();
        ArtleContext context = artleService.loadContext(artle);
        Map<String,String> resource = context.getImageRes();
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n");
        resource.entrySet().forEach(ent->
                sb.append("[")
                        .append(ent.getKey())
                        .append("]: data:image/png;base64,")
                        .append(ent.getValue())
                        .append("\n"));
        String content = renderer.render(parser.parse(context.getContent()+"\n"+sb.toString()));
        content = "<!doctype html><html><head><style>"+config.getMdStyleContent()+"</style></head>"
                    +"<body ondragstart='return false;'>"+content+"</body></html>";
        readView.getWebView().getEngine().loadContent(content);
    }

}
