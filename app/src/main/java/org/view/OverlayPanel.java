package org.view;

import javax.swing.*;
import java.awt.*;

public class OverlayPanel extends JPanel {
    private final CardLayout layout;

    public OverlayPanel() {
        layout = new CardLayout();
        setLayout(layout);
        setOpaque(false); // trasparente per far vedere sotto il contenuto
		setVisible(false);
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
