package org.swdc.note.ui.controllers;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.swdc.fx.services.Service;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class GlobalKeyListener extends Service implements NativeKeyListener {

    @Override
    public void initialize() {
        try {
            LogManager.getLogManager().reset();
            Logger exlogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            exlogger.setLevel(Level.OFF);
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
            logger.info("native hook is registered.");
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
        //logger.info(NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode()) + " pressed");
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
