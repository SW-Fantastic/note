package org.swdc.note.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.swdc.note.app.file.Formatter;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormatterService {

    @Autowired
    private List<Formatter> formatters;

    public Formatter getDocumentFormatterByType(Class clazz, boolean batch) {
        for (Formatter formatter : formatters) {
            if (formatter.supportObject(clazz) && formatter.isBatch() == batch) {
                return formatter;
            }
        }
        return null;
    }

    public Formatter getDocumentFormatterByExtension(String extension, boolean batch) {
        for (Formatter formatter : formatters) {
            if (formatter.supportExtension(extension) && formatter.isBatch() == batch) {
                return formatter;
            }
        }
        return null;
    }

    public List<Formatter> getDocumentFormatters() {
        return formatters.stream().filter(formatter -> !formatter.isBatch()).collect(Collectors.toList());
    }

    public List<Formatter> getBatchFormatters(){
        return formatters.stream().filter(formatter -> formatter.isBatch()).collect(Collectors.toList());
    }

    public List<Formatter> getAllFormatters() {
        return formatters;
    }

}
