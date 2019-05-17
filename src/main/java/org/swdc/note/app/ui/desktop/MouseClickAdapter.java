package org.swdc.note.app.ui.desktop;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by lenovo on 2019/5/16.
 */
public interface MouseClickAdapter extends MouseListener {

    void mouseClicked(MouseEvent e);

    @Override
    default void mousePressed(MouseEvent e) {

    }

    @Override
    default void mouseReleased(MouseEvent e) {

    }

    @Override
    default void mouseEntered(MouseEvent e) {

    }

    @Override
    default void mouseExited(MouseEvent e) {

    }
}
