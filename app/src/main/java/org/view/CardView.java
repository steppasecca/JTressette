package org.view;

import org.model.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CardView extends JComponent {
    private static final long serialVersionUID = 1L;
    private Card card;
    private BufferedImage image; // cached image for this card (from ImageCache)

    public CardView(Card card) {
        this.card = card;
        if (card != null) this.image = ImageCache.getImageForCard(card);
        setPreferredSize(new Dimension(60, 90));
    }

    public void setCard(Card card) {
        this.card = card;
        this.image = (card == null) ? null : ImageCache.getImageForCard(card);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = getWidth();
        int h = getHeight();
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            // anti-aliasing / quality hints
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            if (image != null) {
                // scale preserving aspect ratio
                int iw = image.getWidth();
                int ih = image.getHeight();
                double scale = Math.min((double) w / iw, (double) h / ih);
                int dw = (int) (iw * scale);
                int dh = (int) (ih * scale);
                int dx = (w - dw) / 2;
                int dy = (h - dh) / 2;
                g2.drawImage(image, dx, dy, dw, dh, null);
            } else {
                // placeholder: rectangle + text
                g2.setColor(new Color(220,220,220));
                g2.fillRoundRect(0,0,w-1,h-1,8,8);
                g2.setColor(Color.DARK_GRAY);
                g2.drawRoundRect(0,0,w-1,h-1,8,8);
                String txt = (card == null) ? "–" : card.getRank().name().toLowerCase() + " " + card.getSuit().name().toLowerCase();
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(txt);
                g2.drawString(txt, Math.max(4, (w-tw)/2), h/2);
            }
        } finally {
            g2.dispose();
        }
    }
}
