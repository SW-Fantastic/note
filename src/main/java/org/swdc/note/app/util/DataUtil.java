package org.swdc.note.app.util;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.swdc.note.app.ui.UIConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

/**
 * 提供数据更新的通用工具方法
 */
public class DataUtil {

    public static <T> T updateProperties(T src,T target){
        Class clazz = src.getClass();
        ArrayList<Field> fields = new ArrayList<>();
        while (clazz!=null){
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        for(Field field : fields ) {
            try {
                if(isProperties(src.getClass(),field.getName())&&isProperties(target.getClass(),field.getName())){
                    PropertyDescriptor pds = new PropertyDescriptor(field.getName(),src.getClass());
                    PropertyDescriptor targetPds = new PropertyDescriptor(field.getName(),target.getClass());
                    Method targetReader = targetPds.getReadMethod();
                    Method tatgetWriter = targetPds.getWriteMethod();
                    Method reader = pds.getReadMethod();
                    Object val = reader.invoke(src);
                    Object valCurr = targetReader.invoke(target);
                    if (val == null || val.equals("") || val.equals(valCurr)){
                        continue;
                    }
                    tatgetWriter.invoke(target,val);
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        return target;
    }

    public static <T> boolean isEquals(T src,T target){
        Class clazz = src.getClass();
        ArrayList<Field> fields = new ArrayList<>();
        boolean isEqual = true;
        while (clazz!=null){
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        for(Field field : fields ) {
            try {
                if(isProperties(src.getClass(),field.getName())&&isProperties(target.getClass(),field.getName())){
                    PropertyDescriptor pds = new PropertyDescriptor(field.getName(),src.getClass());
                    PropertyDescriptor targetPds = new PropertyDescriptor(field.getName(),target.getClass());
                    Method targetReader = targetPds.getReadMethod();
                    Method reader = pds.getReadMethod();
                    Object val = reader.invoke(src);
                    Object valCurr = targetReader.invoke(target);
                    if (val == null || val.equals("") || val.equals(valCurr)){
                        continue;
                    }
                    isEqual = false;
                }
            }catch (Exception e){
                isEqual = false;
            }
        }
        return isEqual;
    }

    private static boolean isProperties(Class clazz,String name){
        try {
            PropertyDescriptor pds = new PropertyDescriptor(name,clazz);
            pds.getReadMethod();
            pds.getWriteMethod();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 写配置文件，用户配置修改之后用于更新properties文件。
     * @param config 配置数据
     * @throws Exception
     */
    public static void writeConfigProp(UIConfig config) throws Exception{
        Properties props = new Properties();
        props.load(new FileInputStream("configs/config.properties"));
        Properties themeConfig = new Properties();
        themeConfig.load(new FileInputStream("configs/theme/" + config.getTheme() + "/config.properties"));
        if (!props.get("app.theme").toString().equals(config.getTheme())){
            String themeBg = themeConfig.getProperty("app.background");
            if (themeConfig.get("app.background") != null){
                config.setBackground(themeBg);
            }
        }
        props.setProperty("app.theme",config.getTheme());
        props.setProperty("app.background",config.getBackground());
        props.setProperty("app.mode",config.getMode());
        props.setProperty("app.run-in-background",config.getRunInBackground().toString());
        props.setProperty("app.editor-font-size", config.getEditorFontSize() + "");
        props.setProperty("app.win-styled-popup", config.getWindStyledPopup() + "");
        props.store(new FileOutputStream("configs/config.properties"),"this is the configure file to keep users special state");
    }

    /**
     * LateXMath公式生成Base64图片
     * @param funcStr 公式
     * @return 字符串
     * @throws Exception
     */
    public static String compileFunc(String funcStr) {
        try {
            TeXFormula formula = new TeXFormula(funcStr);
            BufferedImage img = (BufferedImage) formula.createBufferedImage(funcStr, TeXConstants.STYLE_DISPLAY,18, Color.BLACK,Color.WHITE);
            ByteArrayOutputStream bot = new ByteArrayOutputStream();
            ImageIO.write(img,"PNG",bot);
            byte[] buffer = bot.toByteArray();
            return Base64.getEncoder().encodeToString(buffer);
        }catch (Exception e){
           return null;
        }
    }

}
