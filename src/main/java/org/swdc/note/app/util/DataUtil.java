package org.swdc.note.app.util;

import org.swdc.note.app.ui.UIConfig;

import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
        props.setProperty("app.theme",config.getTheme());
        props.setProperty("app.background",config.getBackground());
        props.setProperty("app.mode",config.getMode());
        props.store(new FileOutputStream("configs/config.properties"),"this is the configure file to keep users special state");
    }

}
