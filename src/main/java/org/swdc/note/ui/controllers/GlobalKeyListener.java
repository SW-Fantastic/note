package org.swdc.note.ui.controllers;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.swdc.fx.anno.Listener;
import org.swdc.fx.event.ConfigRefreshEvent;
import org.swdc.fx.services.Service;
import org.swdc.note.config.AppConfig;
import org.swdc.note.core.entities.Article;
import org.swdc.note.core.entities.ArticleContent;
import org.swdc.note.ui.component.KeyboardPropertyEditor;
import org.swdc.note.ui.view.ArticleEditorView;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GlobalKeyListener extends Service implements NativeKeyListener {

    private HashMap<Integer, Boolean> keyMap = new HashMap<>();

    private Map<ObjectProperty<Integer[]>, Boolean> bindsMap = new HashMap<>();

    private List<Integer> editorKeys = new ArrayList<>();

    @Listener(ConfigRefreshEvent.class)
    public void onKeyRefresh(ConfigRefreshEvent event) {
        AppConfig config = findComponent(AppConfig.class);
        String keys = config.getFastEditKey();
        editorKeys = Arrays.asList(KeyboardPropertyEditor.stringToKeyCode(keys));
    }

    @Override
    public void initialize() {
        try {
            LogManager.getLogManager().reset();
            Logger exlogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            exlogger.setLevel(Level.OFF);
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            logger.info("native hook is registered.");

            AppConfig config = findComponent(AppConfig.class);
            String keys = config.getFastEditKey();
            editorKeys = Arrays.asList(KeyboardPropertyEditor.stringToKeyCode(keys));
        } catch (Exception e) {
            logger.error("fail to register native hook: ", e);
        }
    }

    @Override
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
                .filter(e -> e.getValue())
                .map(e -> e.getKey())
                .collect(Collectors.toList()).toArray(new Integer[0]);
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
                        ArticleEditorView editorView = findView(ArticleEditorView.class);
                        editorView.addArticle(article);
                        editorView.show();
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
