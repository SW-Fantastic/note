package org.swdc.note.app.ui.desktop;


import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.note.app.event.ViewChangeEvent;
import org.swdc.note.app.ui.UIConfig;
import org.swdc.note.app.ui.view.StartEditView;
import org.swdc.note.app.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * 托盘图标，使用JPopupMenu代替AWT的Menu，因为AWT的Menu会乱码
 */
@Component
public class TrayMenuIcon extends TrayIcon {

    private JPopupMenu popupMenu;

    private UIConfig config;

    private PopupMenu awtMenu;

    @Autowired
    private StartEditView editView;

    @Autowired
    public TrayMenuIcon(UIConfig config) {
        super(UIConfig.getTrayImage(),"幻想笔记");
        this.config = config;
        this.addMouseListener((MouseClickAdapter)this::onIconClick);
        this.popupMenu = new JPopupMenu();
        JMenuItem itemOpen = new JMenuItem("创建记录");
        itemOpen.addActionListener(this::createNewDoc);
        popupMenu.add(itemOpen);
        JMenuItem itemShow = new JMenuItem("打开主界面");
        itemShow.addActionListener(this::showMainView);
        popupMenu.add(itemShow);
        JMenuItem itemExit = new JMenuItem("退出");
        itemExit.addActionListener(this::exit);
        if (config.getAwtFont() != null) {
            Font font = config.getAwtFont().deriveFont(12f);
            itemOpen.setFont(font);
            itemShow.setFont(font);
            itemExit.setFont(font);
        }
        popupMenu.add(itemExit);
        popupMenu.setInvoker(popupMenu);
        popupMenu.addMouseListener((MouseExitedAdapter)this::onMenuMouseExited);

        awtMenu = new PopupMenu();
        MenuItem itemMenuOpen = new MenuItem("创建文档");
        MenuItem itemMenuView = new MenuItem("打开主界面");
        MenuItem itemMenuExit = new MenuItem("退出");

        itemMenuOpen.addActionListener(this::createNewDoc);
        itemMenuView.addActionListener(this::showMainView);
        itemMenuExit.addActionListener(this::exit);

        awtMenu.add(itemMenuOpen);
        awtMenu.add(itemMenuView);
        awtMenu.add(itemMenuExit);
    }

    private void onIconClick(MouseEvent event) {
        try {
            if (isWindowsStyledPopup()) {
                popupMenu.setLocation(event.getX(), event.getY() - (int)popupMenu.getPreferredSize().getHeight() / popupMenu.getComponents().length * popupMenu.getComponents().length + 1);
            } else {
                popupMenu.setLocation(event.getX(), event.getY());
            }
            if (event.getButton() == MouseEvent.BUTTON3) {
                if (event.getClickCount() == 1) {
                    popupMenu.setVisible(true);
                }
            } else {
                if (event.getClickCount() == 2 && config.getWinStyledPopup()) {
                    this.showMainView(null);
                } else {
                    popupMenu.setVisible(true);
                }
            }
        } catch (NullPointerException ex) {
            if (this.getPopupMenu() == null) {
                this.setPopupMenu(awtMenu);
            }
        }

    }

    private void onMenuMouseExited(MouseEvent event) {
        int x = event.getXOnScreen();
        int y = event.getYOnScreen();
        double menuX = popupMenu.getLocationOnScreen().getX();
        double menuY = popupMenu.getLocationOnScreen().getY();
        double menuWidth = popupMenu.getPreferredSize().getWidth();
        double menuHeight = popupMenu.getPreferredSize().getHeight();
        if (x < menuX) {
            popupMenu.setVisible(false);
            return;
        }
        if (x > menuX + menuWidth) {
            popupMenu.setVisible(false);
            return;
        }
        if (y < menuY) {
            if (isWindowsStyledPopup()) {
                popupMenu.setVisible(false);
                return;
            }
        }
        if (y > menuY + menuHeight) {
            if (!isWindowsStyledPopup()) {
                popupMenu.setVisible(false);
                return;
            }
        }
    }

    private void showMainView(ActionEvent event) {
        Platform.runLater(()->{
            if(GUIState.getStage().isShowing()){
                GUIState.getStage().requestFocus();
            }else{
                GUIState.getStage().show();
            }
        });
    }

    private void createNewDoc(ActionEvent event) {
        Platform.runLater(()->{
            if(UIUtil.isClassical()){
                if(editView.getStage().isShowing()){
                    editView.getStage().requestFocus();
                }else{
                    editView.getStage().show();
                }
            }else{
                if(GUIState.getStage().isShowing()){
                    GUIState.getStage().requestFocus();
                }else{
                    GUIState.getStage().show();
                }
                config.publishEvent(new ViewChangeEvent("EditView"));
            }
        });
    }

    private void exit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    private boolean isWindowsStyledPopup() {
       return this.config.getWinStyledPopup();
    }

}
