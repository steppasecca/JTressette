package org.view;

import org.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

/**
 * view principale par la mano del tressette
 */

public class GamePanel extends JPanel {

	private final JPanel scorePanel = new JPanel(new GridLayout(0,1));
	private TablePanel tablePanelComponent;
	private final JPanel handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
	private final JTextArea logArea = new JTextArea(8,20);
	private final JButton passButton = new JButton("passa il turno (debug)");

	private Consumer<Card> cardClickListener = c -> {};


	public GamePanel(){
		super(new BorderLayout(8,8));
		initGP();
	}

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
		controls.add(passButton);
		bottom.add(bottomTop,BorderLayout.CENTER);
		bottom.add(controls, BorderLayout.SOUTH);
		
        // East: log
        JPanel east = new JPanel(new BorderLayout());
        east.add(new JLabel("Log"), BorderLayout.NORTH);
        logArea.setEditable(false);
        east.add(new JScrollPane(logArea), BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(bottom, BorderLayout.SOUTH);
        add(east, BorderLayout.EAST);

        // default handlers
        passButton.addActionListener(e -> appendLog("Utente ha premuto Passa (debug)"));
	}

	public void setCardClickListener(Consumer<Card> listener){
		this.cardClickListener = listener;
	}

	/**
     * Aggiorna la visuale della mano umana.
     * Deve essere chiamato sull'EDT; il controller fa SwingUtilities.invokeLater se necessario.
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
				b = new JButton(cardToString(c));
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
            String text = t.getTeamName() + ": " + t.calculateTeamScore();
            scorePanel.add(new JLabel(text));
        }
        scorePanel.revalidate();
        scorePanel.repaint();
    }

	public void setTablePanel(TablePanel tp) {
		if (tablePanelComponent != null) remove(tablePanelComponent);
		tablePanelComponent = tp;
		add(tablePanelComponent, BorderLayout.CENTER); 
		revalidate();
		repaint();
	}
// nuovo metodo per aggiornare la tabella con le giocate attuali
public void updateTable(List<Play> plays, List<Player> players) {
    if (tablePanelComponent == null) return;
    // pulisci slot ultima carta prima di ri-disegnare
    // mostra ogni play: trovi l'indice del player nella lista 'players'
    for (Play p : plays) {
        Player pl = p.getPlayer();
        int idx = players.indexOf(pl);
        if (idx >= 0) {
            tablePanelComponent.showPlayedCard(idx, p.getCard());
        }
    }
    // se la presa è vuota, potresti voler pulire le ultime carte:
    if (plays.isEmpty()) {
        // facoltativo: pulire last played per tutti (non implementato)
    }
}

// delega per mostrare la carta vincente (usato da GameController)
public void showPlayedCard(int playerIndex, org.model.Card card) {
    if (tablePanelComponent != null) {
        tablePanelComponent.showPlayedCard(playerIndex, card);
    } else {
        appendLog("tablePanel non inizializzato");
    }
}
	// aggiornamento del giocatore corrente chiamato dal controller osservatore:
	public void setCurrentPlayer(int playerIndex) {
		if (tablePanelComponent != null) {
			tablePanelComponent.setCurrentPlayer(playerIndex);
		} else {
			appendLog("tablePanel non inizializzato");
		}
	}

    public void setCurrentPlayer(String playerName) {
        appendLog("Turno corrente: " + playerName);
    }

    public void appendLog(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private String cardToString(org.model.Card c) {
        return c.getRank().name() + " di " + c.getSuit().name();
    }
}


