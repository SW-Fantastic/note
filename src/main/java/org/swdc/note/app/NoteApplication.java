package org.swdc.note.app;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.application.Platform;
import javafx.scene.image.Image;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.swdc.note.app.ui.Splash;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.StartView;
import org.swdc.note.app.ui.view.classes.ClStartView;
import org.swdc.note.app.ui.view.quick.QuickFrame;
import org.swdc.note.app.util.UIUtil;

import javax.annotation.PostConstruct;
import java.util.Collection;

@SpringBootApplication
public class NoteApplication extends AbstractJavaFxApplicationSupport {

    @Autowired
    private QuickFrame quickFrame;

    public static void main(String[] args) throws Exception{
        if (UIUtil.isUseFloat()){
            BeautyEyeLNFHelper.launchBeautyEyeLNF();
            // 取消窗口退出后直接关闭程序
            Platform.setImplicitExit(false);
        }
        /*
        * 判断启动模式，classical模式和normal两种，
        * 他们会共用一部分controller，这会导致controller冲突
        * 因此部分方法会失效，为了防止这种问题，需要使用NotViewClassicalCondition
        * 结合conditional注解屏蔽一部分view
        * */
        if(UIUtil.isClassical()){
            launch(NoteApplication.class,ClStartView.class,new Splash(),args);
        }else{
            launch(NoteApplication.class,StartView.class,new Splash(),args);
        }
    }

    @PostConstruct
    protected void initUI(){
        if(UIUtil.isUseFloat()){
            quickFrame.setVisible(true);
        }
    }

    @Override
    public Collection<Image> loadDefaultIcons() {
        try {
            return UIConfig.getImageIcons();
        }catch (Exception e){
            return super.loadDefaultIcons();
        }
    }

}
