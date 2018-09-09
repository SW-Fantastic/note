package org.swdc.note.app.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

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
        fields.forEach(field -> {
            try {
                if(isProperties(src.getClass(),field.getName())){
                    PropertyDescriptor pds = new PropertyDescriptor(field.getName(),src.getClass());
                    Method reader = pds.getReadMethod();
                    Method writer = pds.getWriteMethod();
                    Object val = reader.invoke(src);
                    Object valCurr = reader.invoke(target);
                    if (val == null || val.equals("") || val.equals(valCurr)){
                        return;
                    }
                    writer.invoke(target,val);
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        });
        return target;
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

}
