package org.view;

import org.model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * componente grafico che rappresenta il tavolo da gioco
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
		setBackground(new Color(20,80,55));
		initSlots(players);
	}
	 
	/**
	 * metodo privato di utilità per inizializzare e aggiungere i playerSlot
	 *
	 * @param players
	 * @return void
	 */
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
			//se le dimensioni sono 0 aggiungo un listener per controllare se il pannello 
			//viene ridimensionato nel qual caso viene richiamato doLayoutSlots()
			addComponentListener(new java.awt.event.ComponentAdapter() {
				@Override
				public void componentResized(java.awt.event.ComponentEvent e) {
					doLayoutSlots();
				}
			});
			return;
		}
		int slotW = 140, slotH = 220;
		if (slotCount == 2){
			// se sono due giocatori uno al centro in alto e uno al centro in basso
            slots.get(0).setBounds((w - slotW)/2, 10, slotW, slotH);
            slots.get(1).setBounds((w - slotW)/2, h - slotH - 10, slotW, slotH);
        } else {
            // nel caso siano quattro uno centrale in alto e uno in basso 
			// poi uno centrale a destra e uno a sinistra
            slots.get(0).setBounds((w - slotW)/2, 10, slotW, slotH);
            slots.get(2).setBounds((w - slotW)/2, h - slotH - 10, slotW, slotH);
            slots.get(1).setBounds(w - slotW - 10, (h - slotH)/2, slotW, slotH);
            slots.get(3).setBounds(10, (h - slotH)/2, slotW, slotH);
        }
    }

	/**
	 * ricalcola il layout
	 */
    @Override
    public void invalidate() {
        super.invalidate();
        // dopo resize ricalcola posizioni
		// invokeLater per evitare concorrenza
        SwingUtilities.invokeLater(this::doLayoutSlots);
    }

	/**
	 * metodo che permette al controller di comunicare il giocatore corrente
	 *
	 * @param playerIndex indice del giocatore da evidenziare
	 * @return void
	 */
    public void setCurrentPlayer(int playerIndex) {
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).setHighlighted(i == playerIndex);
        }
    }

	/**
	 * metodo che permette al controller di settar l'ultima carta giocata da un giocatore
	 *
	 * @param playerIndex int
	 * @param card Card
	 * @return void
	 */
    public void showPlayedCard(int playerIndex, Card card) {
        if (playerIndex < 0 || playerIndex >= slots.size()) return;
        slots.get(playerIndex).setLastPlayedCard(card);
        // opzionale: repaint centrale per animazione
        repaint();
    }

}
