package org.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OverlayPanel extends JPanel {
    private final CardLayout layout;

    public OverlayPanel() {
        layout = new CardLayout();
        setLayout(layout);
        setOpaque(false); // trasparente per far vedere sotto il contenuto
		setVisible(false);
        // Rendi il glass pane focusable e bloccante
        setFocusable(true);
        
        // Blocca tutti gli eventi del mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { e.consume(); }
            @Override
            public void mousePressed(MouseEvent e) { e.consume(); }
            @Override
            public void mouseReleased(MouseEvent e) { e.consume(); }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) { e.consume(); }
            @Override
            public void mouseDragged(MouseEvent e) { e.consume(); }
        });
        
        // Blocca eventi tastiera
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { e.consume(); }
            @Override
            public void keyReleased(KeyEvent e) { e.consume(); }
            @Override
            public void keyTyped(KeyEvent e) { e.consume(); }
        });
    }

    public void addOverlay(String name, JComponent comp) {
        add(comp, name);
    }

    public void showOverlay(String name) {
        layout.show(this, name);
        setVisible(true);
    }

    public void hideOverlay() {
        setVisible(false);
    }
}
