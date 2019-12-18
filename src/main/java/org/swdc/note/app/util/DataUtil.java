package org.swdc.note.app.util;

import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.swdc.note.app.configs.ConfigProp;
import org.swdc.note.app.ui.UIConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

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

    public static byte[] loadHttpData(String url) throws Exception{

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.connect();
        int code = connection.getResponseCode();
        if(code == 200){
            DataInputStream din = new DataInputStream(connection.getInputStream());
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = din.read(buff)) > 0){
                byteArrayOutputStream.write(buff,0,len);
            }
            byteArrayOutputStream.flush();
            din.close();
        }
        connection.disconnect();
        return byteArrayOutputStream.toByteArray();
    }

    public static File downloadHttpContent(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.connect();
        int code = connection.getResponseCode();
        if(code == 200){
            FileOutputStream outputStream = null;
            try {
                String contentType = connection.getHeaderField("Content-Type");
                MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
                if (contentType.contains(";")) {
                    contentType = contentType.split(";")[0].trim();
                }
                MimeType type = allTypes.forName(contentType);
                File tempFile = new File("./data/temp" + new Date().getTime() + type.getExtension());
                outputStream = new FileOutputStream(tempFile);
                byte[] buff = new byte[1024];
                DataInputStream din = new DataInputStream(connection.getInputStream());
                while (din.read(buff) > 0){
                    outputStream.write(buff);
                }
                outputStream.flush();
                outputStream.close();
                connection.disconnect();
                return tempFile;
            } catch (Exception ex) {
                if (outputStream != null) {
                    outputStream.close();
                }
                throw ex;
            }
        }
        connection.disconnect();
        return null;
    }

    public static void saveConfigFile(Object config) throws Exception {
        Field[] fields = config.getClass().getDeclaredFields();
        ConfigurationProperties prop = config.getClass().getAnnotation(ConfigurationProperties.class);
        PropertySource propSource = config.getClass().getAnnotation(PropertySource.class);
        String name = propSource.value()[0].substring(propSource.value()[0].lastIndexOf("/"));
        String prefix = prop.prefix();
        Properties props = new Properties();
        props.load(new FileInputStream("./configs/" + name));
        for(Field field: fields) {
            if (field.getAnnotation(ConfigProp.class) == null) {
                continue;
            }
            PropertyDescriptor desc = new PropertyDescriptor(field.getName(),config.getClass());
            props.setProperty(prefix +"."+ field.getAnnotation(ConfigProp.class).propName(), desc.getReadMethod().invoke(config).toString());
        }
        props.store(new FileOutputStream("./configs/" + name), "");
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
