package org.view;

import org.model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * TablePanel pannello parametrico che dispone gli slots per i giocatori/giocatrici
 */

public class TablePanel extends JPanel {

	private final List<PlayerSlot> slots = new ArrayList<>();
	private final int slotCount;

	public TablePanel(List<Player> players){
		if(players==null || !(players.size()==2 || players.size()==4)){
			throw new IllegalArgumentException("TablePanel supporta 2 o 4 giocatori/giocatrici");
		}
		this.slotCount = players.size();
		setLayout(null);
		setBackground(new Color(34,120,60));
		initSlots(players);
	}
	 
	private void initSlots(List<Player> players){
		for(int i = 0;i<players.size();i++){
			PlayerSlot ps = new PlayerSlot(players.get(i));
			slots.add(ps);
			add(ps);
		}
		doLayoutSlots();
	}

	private void doLayoutSlots() {
		int w = getWidth();
		int h = getHeight();

		if(w==0 || h==0){
			addComponentListener(new java.awt.event.ComponentAdapter() {
				@Override
				public void componentResized(java.awt.event.ComponentEvent e) {
					doLayoutSlots();
				}
			});
			return;
		}
		int slotW = 140, slotH = 80;
		if (slotCount == 2){
            // giocatore 0 in alto, giocatore 1 in basso
            slots.get(0).setBounds((w - slotW)/2, 10, slotW, slotH);
            slots.get(1).setBounds((w - slotW)/2, h - slotH - 10, slotW, slotH);
        } else {
            // 4 giocatori: 0=north, 1=east, 2=south, 3=west
            slots.get(0).setBounds((w - slotW)/2, 10, slotW, slotH);
            slots.get(2).setBounds((w - slotW)/2, h - slotH - 10, slotW, slotH);
            slots.get(1).setBounds(w - slotW - 10, (h - slotH)/2, slotW, slotH);
            slots.get(3).setBounds(10, (h - slotH)/2, slotW, slotH);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        // dopo resize ricalcola posizioni
        SwingUtilities.invokeLater(this::doLayoutSlots);
    }

    // API pubblica usata dalla GamePanel/controller
    public void setCurrentPlayer(int playerIndex) {
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).setHighlighted(i == playerIndex);
        }
    }

    public void showPlayedCard(int playerIndex, Card card) {
        // mostra la carta al centro (potresti animarla)
        if (playerIndex < 0 || playerIndex >= slots.size()) return;
        // mostra nell'area della slot la carta come ultima giocata
        slots.get(playerIndex).setLastPlayedCard(card);
        // opzionale: repaint centrale per animazione
        repaint();
    }

    public void updateHandCount(int playerIndex, int count) {
        if (playerIndex < 0 || playerIndex >= slots.size()) return;
        slots.get(playerIndex).setCardCount(count);
    }
}
