package org.view;

import org.model.*;
import org.controller.GameController;
import org.util.ModelEventMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.Observer;
import java.util.Observable;
import java.util.Collections;

/**
 * componente grafico principale per il gioco
 */
public class GamePanel extends JPanel implements Observer{

	//controller
	private GameController controller;
	private final JPanel scorePanel = new JPanel(new GridLayout(0,1));
	private TablePanel tablePanelComponent;
	private final JPanel handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
	private final JTextArea logArea = new JTextArea(8,20);

	//gestione pausa
	private JButton pauseButton;
	private PauseMenuPanel pauseMenuPanel;
	private Consumer<PauseMenuPanel> pauseMenuListener;

	//listener click sulle carte
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

		pauseButton = new JButton("pausa");
		pauseButton.addActionListener(e -> {
			JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(GamePanel.this);
			if (topFrame != null && topFrame.getGlassPane() instanceof PauseMenuPanel menuPanel) {
				menuPanel.setVisible(true); // mostra menuPanel
			}
		});
		top.add(pauseButton,BorderLayout.EAST);
		


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
	 * mostra il pannello pauseMenuPanel
	 */
    public void showPauseMenuPanel() {
        if (pauseMenuPanel == null) {
            pauseMenuPanel = new PauseMenuPanel();
            if (pauseMenuListener != null) {
                pauseMenuListener.accept(pauseMenuPanel);
            }
        }
        setLayout(new OverlayLayout(this));
        add(pauseMenuPanel, 0); // sopra tutto
        revalidate();
        repaint();
    }

	/**
	 * nasconde il pannello pauseMenuPanel
	 */
	public void hidePauseMenuPanel(){

		if(pauseMenuPanel!=null) { 
			remove(pauseMenuPanel);
			pauseMenuPanel = null;
			revalidate();
			repaint();
		}
	}

	/**
	 * getter per pauseMenuPanell
	 * @return pauseMenuPanel
	 */
	public PauseMenuPanel getPauseMenuPanel() {return this.pauseMenuPanel;}

	public void setPauseMenuListener(Consumer<PauseMenuPanel> listener){
		this.pauseMenuListener = listener;
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
	 * prova
	 */
	public void playCard(int playerIdx, org.model.Card card){
		if(tablePanelComponent!=null){
			tablePanelComponent.playAnimatedCard(playerIdx, card);
		} else { 
			appendLog("tb non inizializzato"); 
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

    /**
     * Imposta il controller per permettere alla vista di delegare azioni.
     * @param controller Il GameController che gestisce la logica.
     */
    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void update(Observable o, Object arg) {
        SwingUtilities.invokeLater(() -> {
            if (arg instanceof ModelEventMessage msg) {
                switch (msg.getEvent()) {
                    case CARDS_DEALT -> handleCardsDealt(msg.getPayload());
                    case CARD_PLAYED -> handleCardPlayed(msg.getPayload());
                    case TURN_STARTED -> handleTurnStarted(msg.getPayload());
                    case TRICK_ENDED -> handleTrickEnded(msg.getPayload());
                    case ROUND_STARTED -> handleRoundStarted();
                    case ROUND_ENDED -> handleRoundEnded((List<Team>) msg.getPayload());
                    case GAME_OVER -> handleGameOver();
                    // Aggiunto un evento generico per un refresh completo
                    case GAME_STATE_UPDATE -> handleGameStateUpdate(msg.getPayload());
                }
            }
        });
    }


	/**
     * Gestisce l'evento CARDS_DEALT.
     * @param payload Si aspetta una List<Card> rappresentante la mano del giocatore umano.
     */
    private void handleCardsDealt(Object payload) {
        if (payload instanceof List) {
            List<Card> hand = (List<Card>) payload;
            updateHand(hand);
            appendLog("Le carte sono state distribuite.");
        }
    }
    
    /**
     * Gestisce l'evento GAME_STATE_UPDATE per un refresh completo.
     * @param payload Si aspetta un Object[] {List<Card> hand, List<Play> plays, List<Player> players, List<Team> teams, Integer currentPlayerIndex}
     */
    private void handleGameStateUpdate(Object payload) {
        if (payload instanceof Object[] data) {
            updateHand((List<Card>) data[0]);
            updateTable((List<Play>) data[1], (List<Player>) data[2]);
            updateScores((List<Team>) data[3]);
            setCurrentPlayer((Integer) data[4]);
            appendLog("Vista aggiornata completamente.");
        }
    }

    /**
     * Gestisce l'evento CARD_PLAYED.
     * @param payload Si aspetta un Object[] {Play play, List<Player> players}.
     */
    private void handleCardPlayed(Object payload) {
        if (payload instanceof Object[] data) {
            Play play = (Play) data[0];
            List<Player> players = (List<Player>) data[1];
            appendLog("Il giocatore " + play.getPlayer().getNome() + " ha giocato " + play.getCard().toString());
            int idx = players.indexOf(play.getPlayer());
            if (idx >= 0) {
                playCard(idx, play.getCard());
            }
        }
    }

	
    /**
     * Gestisce l'evento TURN_STARTED.
     * @param payload Si aspetta un Integer con l'indice del giocatore di turno.
     */
    private void handleTurnStarted(Object payload) {
        if (payload instanceof Integer idx) {
            setCurrentPlayer(idx);
        } else {
            System.err.println("handleTurnStarted: Payload non valido.");
        }
    }

	private void handleTrickEnded(Object payload){
		Player winner = null;
		List<Play> plays = Collections.emptyList();
		List<Player> players = Collections.emptyList();

		// gestisce il nuovo payload Object[] {winner, playsSnapshot}
		if (payload instanceof Object[]) {
			Object[] arr = (Object[]) payload;
			if (arr.length >= 1 && arr[0] instanceof Player) {
				winner = (Player) arr[0];
			}
			if (arr.length >= 2 && arr[1] instanceof List) {
				plays = (List<Play>) arr[1];
				for(Play play: plays){
					players.add(play.getPlayer());
				}

			}
		} else if (payload instanceof Player) { //else if di sicurezza se si torna un payload con solo i player
			winner = (Player) payload;
		}

		if (winner != null) {
			appendLog("Ha preso questa mano: " + winner.getNome() 
					+ " della squadra: " + winner.getTeam().getTeamName());
		} else {
			appendLog("Ha preso questa mano: (nessun winner nel payload)");
		}

		// mostra subito le carte della mano (snapshot)
		
		updateTable(plays, players);

		// tienile a video per 1 secondo, poi pulisci e lascia partire il nuovo turno
		new javax.swing.Timer(2000, e -> {
			((javax.swing.Timer) e.getSource()).stop();
			updateTable(Collections.emptyList(), players);
		}) {{
			setRepeats(false);
			start();
		}};
	}

    /**
     * Gestisce l'evento ROUND_STARTED.
     */
    private void handleRoundStarted() {
        appendLog("Nuovo round iniziato!");
        // La pulizia del tavolo e della mano avverrà con i successivi eventi (es. CARDS_DEALT)
    }

	/**
	 * gestisce l'evento ROUND_ENDED
	 * @param teams
	 */
	private void handleRoundEnded(List<Team> teams){
		updateScores(teams); 
		JOptionPane.showMessageDialog(this, "Fine della round! Si procede al prossimo.");
	}

	    private void handleGameOver() {
        // Qui si potrebbe mostrare un pannello di fine partita.
        JOptionPane.showMessageDialog(this, "La partita è finita!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        appendLog("Partita terminata.");
    }
    

}
