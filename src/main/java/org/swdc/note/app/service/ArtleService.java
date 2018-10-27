package org.swdc.note.app.service;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swdc.note.app.entity.Artle;
import org.swdc.note.app.entity.ArtleContext;
import org.swdc.note.app.entity.ArtleType;
import org.swdc.note.app.repository.ArtleContextRepository;
import org.swdc.note.app.repository.ArtleRepository;
import org.swdc.note.app.repository.ArtleTypeRepository;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.util.DataUtil;

import java.util.List;
import java.util.Map;

/**
 * 文章服务，提供关于文章的各种操作
 */
@Service
public class ArtleService {

    @Autowired
    private Parser parser;

    @Autowired
    private HtmlRenderer renderer;

    @Autowired
    private UIConfig config;

    @Autowired
    private ArtleRepository artleRepository;

    @Autowired
    private ArtleTypeRepository typeRepository;

    @Transactional
    public List<Artle> loadArtles(ArtleType type){
        type = typeRepository.getOne(type.getId());
        Hibernate.initialize(type.getArtles());
        return type.getArtles();
    }

    @Transactional
    public ArtleContext loadContext(Artle artle){
        artle = artleRepository.getOne(artle.getId());
        Hibernate.initialize(artle.getContext());
        return artle.getContext();
    }

    @Transactional
    public void saveArtle(Artle artle, ArtleContext context){
        if(artle.getId() == null){
            artle.setContext(context);
            artleRepository.save(artle);
            return;
        }
        Artle artleOld = artleRepository.getOne(artle.getId());
        Hibernate.initialize(artleOld.getContext());
        ArtleContext contextOld = artleOld.getContext();
        // 更新持久态对象
        contextOld = DataUtil.updateProperties(context,contextOld);
        contextOld.setImageRes(context.getImageRes());
        artleOld = DataUtil.updateProperties(artle,artleOld);
        artleOld.setContext(contextOld);
        artleOld.setType(typeRepository.getOne(artle.getType().getId()));

        artleRepository.save(artleOld);
    }

    @Transactional
    public void deleteArtle(Artle artle){
        artle = artleRepository.getOne(artle.getId());
        artleRepository.delete(artle);
    }

    @Transactional
    public List<Artle> searchArtleByTitle(String key){
        return artleRepository.findByTitleContaining(key);
    }

    public String complie(ArtleContext context){
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
        return content;
    }

}
