package org.view;

import org.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

/**
 * componente grafico principale per il gioco
 */
public class GamePanel extends JPanel {

	private final JPanel scorePanel = new JPanel(new GridLayout(0,1));
	private TablePanel tablePanelComponent;
	private final JPanel handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
	private final JTextArea logArea = new JTextArea(8,20);

	private Consumer<Card> cardClickListener = c -> {};


	public GamePanel(){
		super(new BorderLayout(8,8));
		initGP();
	}

	/**
	 * metodo privato di utilità che inizializza e configura tutti i componenti e "sotto-pannelli"
	 *
	 * @return void
	 */
	private void initGP() {

		//in alto la scoreBoard
		JPanel top = new JPanel(new BorderLayout());
		top.add(new JLabel("punteggi: "),BorderLayout.NORTH);
		top.add(scorePanel,BorderLayout.CENTER);


		//in basso la mano e i pulsanti
		JPanel bottom = new JPanel(new BorderLayout());
		JPanel bottomTop = new JPanel(new BorderLayout());
		bottomTop.add(new Label("Mano: "),BorderLayout.NORTH);
		handPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		bottomTop.add(new JScrollPane(handPanel),BorderLayout.CENTER);

		JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottom.add(bottomTop,BorderLayout.CENTER);
		bottom.add(controls, BorderLayout.SOUTH);

		// a destra l'area di log
		JPanel east = new JPanel(new BorderLayout());
		east.add(new JLabel("Log"), BorderLayout.NORTH);
		logArea.setEditable(false);
		east.add(new JScrollPane(logArea), BorderLayout.CENTER);

		add(top, BorderLayout.NORTH);
		add(bottom, BorderLayout.SOUTH);
		add(east, BorderLayout.EAST);

	}

	/**
	 * litstener per le carte nella mano
	 *
	 * @param listener
	 * @return void
	 */
	public void setCardClickListener(Consumer<Card> listener){
		this.cardClickListener = listener;
	}

	/**
	 * Aggiorna la visuale della mano umana.
	 * @param cards
	 */
	public void updateHand(List<org.model.Card> cards) {
		handPanel.removeAll();
		final int thumbW = 80;
		final int thumbH = 120;
		for (org.model.Card c : cards) {
			BufferedImage img = ImageCache.getImageForCard(c);
			JButton b;
			if (img != null) {
				Image scaled = img.getScaledInstance(thumbW, thumbH, Image.SCALE_SMOOTH);
				b = new JButton(new ImageIcon(scaled));
			} else {
				b = new JButton(c.toString());
			}
			b.setFocusPainted(false);
			b.setBorder(BorderFactory.createEmptyBorder());
			b.addActionListener(e -> cardClickListener.accept(c));
			handPanel.add(b);
		}
		handPanel.revalidate();
		handPanel.repaint();
	}


	/**
	 * Aggiorna la sezione punteggi usando teams (nome e punteggio).
	 */
	public void updateScores(List<Team> teams) {
		scorePanel.removeAll();
		for (Team t : teams) {
			scorePanel.add(new JLabel(t.getTeamName() + ": " + t.getTeamPoints()));
		}
		scorePanel.revalidate();
		scorePanel.repaint();
	}

	/**
	 * 
	 * setter per il tablePanel(forse si può eliminare)
	 * @param tp tablePanel
	 * @return void
	 */
	public void setTablePanel(TablePanel tp) {
		if (tablePanelComponent != null) remove(tablePanelComponent);
		tablePanelComponent = tp;
		add(tablePanelComponent, BorderLayout.CENTER); 
		revalidate();
		repaint();
	}

	/**
	 * chiama i metodi di TablePanel per aggiornare le carte giocate
	 *
	 * @param plays
	 * @param players
	 * @return void
	 */
	public void updateTable(List<Play> plays, List<Player> players) {
		if (tablePanelComponent == null) return;

		// Se la lista è vuota, pulisci subito il tavolo
		if(plays.isEmpty()){
			for(int i = 0; i<players.size(); i++){
				tablePanelComponent.showPlayedCard(i, null);
			}
			return;
		}

		// Altrimenti, mostra le carte giocate
		for (Play p : plays) {
			Player pl = p.getPlayer();
			int idx = players.indexOf(pl);
			if (idx >= 0) {
				tablePanelComponent.showPlayedCard(idx, p.getCard());
			}
		}
	}

	/**
	 * chiama il metodo di TablePanel per mostrare la carta giocata
	 *
	 * @param playerIndex
	 * @param card
	 * @return void
	 */
	public void showPlayedCard(int playerIndex, org.model.Card card) {
		if (tablePanelComponent != null) {
			tablePanelComponent.showPlayedCard(playerIndex, card);
		} else {
			appendLog("tablePanel non inizializzato");
		}
	}

	/**
	 * chiama il matodo di TablePanel per evidenziare il giocatore corrente
	 *
	 * @param playerIndex
	 * @return void
	 */
	public void setCurrentPlayer(int playerIndex) {
		if (tablePanelComponent != null) {
			tablePanelComponent.setCurrentPlayer(playerIndex);
		} else {
			appendLog("tablePanel non inizializzato");
		}
	}

	/**
	 * metodo utile per il debug
	 */
	public void setCurrentPlayer(String playerName) {
		appendLog("Turno corrente: " + playerName);
	}

	/**
	 * metodo per aggiungere messaggi al logPanel (debug)
	 */
	public void appendLog(String msg) {
		logArea.append(msg + "\n");
		logArea.setCaretPosition(logArea.getDocument().getLength());
	}

}


