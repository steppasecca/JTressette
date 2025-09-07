package org.view;


import org.model.Player;
import org.model.Card;
import javax.swing.*;
import java.awt.*;

/**
 * Componente che mostra nickname/avatar/numero carte/ultima carta giocata.
 */
class PlayerSlot extends JPanel {
    private final Player player;
    private JLabel nameLabel;
    private CardView lastCardView;
    private boolean highlighted = false;

    public PlayerSlot(Player p) {
        this.player = p;
        init();
    }

	/**
	 * metodo privato di utilità che inizializza i componenti grafici
	 */
    private void init() {
        setLayout(new BorderLayout(4,4));
        nameLabel = new JLabel(player.getNome(), SwingConstants.CENTER);
		int count = (player.getHand() != null) ? player.getHand().getSizeOfCardSet() : 0;
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
    public void setLastPlayedCard(Card card) {
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

	public Player getPlayer(){
		return this.player;
	}
}
