package org.swdc.note.ui.controllers;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.note.config.AppConfig;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.ui.component.KeyboardPropertyEditor;
import org.swdc.note.ui.events.ConfigRefreshEvent;
import org.swdc.note.ui.view.ArticleEditorView;
import org.swdc.note.ui.view.UIUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GlobalKeyListener implements NativeKeyListener {

    private HashMap<Integer, Boolean> keyMap = new HashMap<>();

    private Map<ObjectProperty<Integer[]>, Boolean> bindsMap = new HashMap<>();

    private List<Integer> editorKeys = new ArrayList<>();

    @Inject
    private ArticleEditorView articleEditorView;

    @Inject
    private AppConfig config;

    @Inject
    private org.slf4j.Logger logger;

    @EventListener(type = ConfigRefreshEvent.class)
    public void onKeyRefresh(ConfigRefreshEvent event) {
        if (event.getMessage() instanceof AppConfig) {
            String keys = config.getFastEditKey();
            editorKeys = Arrays.asList(UIUtils.stringToKeyCode(keys));
        }
    }

    @PostConstruct
    public void initialize() {
        try {
            LogManager.getLogManager().reset();
            Logger exlogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            exlogger.setLevel(Level.OFF);
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            logger.info("native hook is registered.");

            String keys = config.getFastEditKey();
            editorKeys = Arrays.asList(UIUtils.stringToKeyCode(keys));
        } catch (Exception e) {
            logger.error("fail to register native hook: ", e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            GlobalScreen.removeNativeKeyListener(this);
            GlobalScreen.unregisterNativeHook();
            logger.info("native hook has removed");
        } catch (Exception e) {
            logger.error("fail to destroy native hook", e);
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        int code = nativeKeyEvent.getKeyCode();
        keyMap.put(code,true);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

        int code = nativeKeyEvent.getKeyCode();

        Integer[] pressed = keyMap.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .toList()
                .toArray(new Integer[0]);

        boolean valTriggered = false;
        for (ObjectProperty<Integer[]> target : bindsMap.keySet()) {
            if (bindsMap.get(target)) {
                target.setValue(pressed);
                valTriggered = true;
            }
        }
        if (valTriggered) {
            keyMap.put(code, false);
            return;
        }

        try {
            if (editorKeys != null) {
                if(editorKeys.stream().filter(keyMap::containsKey).filter(keyMap::get).count() == editorKeys.size()){
                    Article article = new Article();
                    article.setTitle("无标题：" + new Date().getTime());
                    article.setContent(new ArticleContent());
                    Platform.runLater(() -> {
                        articleEditorView.addArticle(article);
                        articleEditorView.show();
                    });
                }
            }
        } catch (Exception e) {
            logger.error("fail to trigger key event",e);
        }
        keyMap.put(code, false);
    }

    public void enable(ObjectProperty<Integer[]> propExisted,boolean available) {
        if (bindsMap.containsKey(propExisted)) {
            bindsMap.put(propExisted,available);
        }
    }

    public void bindPressedKeys(ObjectProperty<Integer[]> target) {
        bindsMap.put(target,false);
    }

}
