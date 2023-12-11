package org.swdc.note.core.proto;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.swdc.dependency.annotations.MultipleImplement;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

@MultipleImplement(FileURLResolver.class)
public class FileURLResolver extends URLProtoResolver {

    @Inject
    private Logger logger;


    @Override
    public File load(String url) {
        try {
            return Paths.get(new URL(url).toURI()).toFile();
        } catch (Exception e) {
            logger.error("fail to load from file: ",e);
            return null;
        }
    }

    @Override
    public boolean support(String url) {
        try {
            URL target = new URL(url);
            if (target.getProtocol().startsWith("file")){
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
