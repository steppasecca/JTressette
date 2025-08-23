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
    private JLabel cardCountLabel;
    private CardView lastCardView;
    private boolean highlighted = false;

    public PlayerSlot(Player p) {
        this.player = p;
        init();
    }

    private void init() {
        setLayout(new BorderLayout(4,4));
        nameLabel = new JLabel(player.getNome(), SwingConstants.CENTER);
		int count = (player.getHand() != null) ? player.getHand().getSizeOfCardSet() : 0;
		cardCountLabel = new JLabel("♠ " + count, SwingConstants.CENTER);
        lastCardView = new CardView(null);

        add(nameLabel, BorderLayout.NORTH);
        add(lastCardView, BorderLayout.CENTER);
        add(cardCountLabel, BorderLayout.SOUTH);

        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
    }

    public void setCardCount(int c) {
        cardCountLabel.setText("♠ " + c);
    }

    public void setLastPlayedCard(Card card) {
        lastCardView.setCard(card);
        revalidate();
        repaint();
    }

    public void setHighlighted(boolean h) {
        this.highlighted = h;
        setBorder(BorderFactory.createLineBorder(h ? Color.YELLOW : Color.DARK_GRAY, h ? 3 : 2));
        repaint();
    }
}
