package org.swdc.note.core.proto;

import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.swdc.dependency.annotations.MultipleImplement;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

@MultipleImplement(URLProtoResolver.class)
public class HttpURLResolver extends URLProtoResolver {

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

    @Override
    public File load(String url) {
        try {
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
        } catch (Exception e){
        }
        return null;
    }

    @Override
    public boolean support(String url) {
        try {
            URL target = new URL(url);
            if (target.getProtocol().startsWith("http")){
                HttpURLConnection connection = (HttpURLConnection) target.openConnection();
                connection.connect();
                String content = connection.getContentType();
                if (content.contains("html") || content.contains("text/html")) {
                    connection.disconnect();
                    return true;
                }
                connection.disconnect();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

}
