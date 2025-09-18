package org.view;

import javax.swing.*;
import java.awt.*;

/**
 * classe che mostra uno slot per le carte da gioco con realtiva immagine
 */
public class CardSlot extends JPanel{

	private JLabel nameLabel;
    private boolean highlighted = false;
    private CardView lastCardView;

	public CardSlot(String name){
		init(name);
	}

	/**
	 * metodo privato di utilità che inizializza i componenti grafici
	 */
    private void init(String name) {
        setLayout(new BorderLayout(4,4));
        nameLabel = new JLabel(name,SwingConstants.CENTER);
        lastCardView = new CardView(null);
        add(nameLabel, BorderLayout.NORTH);
        add(lastCardView, BorderLayout.CENTER);
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
    }

	/**
	 * setter per lastCardView
	 * @param card
	 * @return void
	 */
    public void setLastPlayedCard(org.model.Card card) {
        lastCardView.setCard(card);
        revalidate();
        repaint();
    }

	/**
	 * evidenzia e de-evidenzia il bordo dello slot dello slot
	 *
	 * @param param the param
	 * @param anotherParam the anotherParam
	 * @return description of return value
	 */
    public void setHighlighted(boolean h) {
        this.highlighted = h;
        setBorder(BorderFactory.createLineBorder(h ? Color.YELLOW : Color.DARK_GRAY, h ? 3 : 2));
        repaint();
    }
}
