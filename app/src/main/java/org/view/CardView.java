
package org.view;

import org.model.Card;
import javax.swing.*;
import java.awt.*;

public class CardView extends JComponent {
    private Card card;

    public CardView(Card card) {
        this.card = card;
        setPreferredSize(new Dimension(60,90));
    }

    public void setCard(Card card) {
        this.card = card;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (card == null) {
            // disegna retro o placeholder
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0,0,getWidth(),getHeight());
            g.setColor(Color.DARK_GRAY);
            g.drawString("–", getWidth()/2 - 4, getHeight()/2);
            return;
        }
        // semplice rappresentazione: mostra seme e valore
        g.setColor(Color.WHITE);
        g.fillRoundRect(0,0,getWidth()-1,getHeight()-1,8,8);
        g.setColor(Color.BLACK);
        String txt = card.toString(); // es. "Asso di Cuori"
        g.drawString(txt, 6, 14);
        // puoi arricchire con immagini
    }
}
