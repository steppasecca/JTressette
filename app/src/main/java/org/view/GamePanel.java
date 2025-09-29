package org.view;

import javax.swing.*;
import java.awt.*;
import java.util.Observer;
import java.util.Observable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import org.model.*;
import org.util.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * classe che mostra il gioco del tressette 
 */
public class GamePanel extends JPanel implements Observer{

	private TablePanel tablePanel;
	private JPanel handPanel; 
	private JPanel scorePanel;

	private Runnable onPause; //callback per il bottone della pausa
	private JButton pauseButton;
	
	//listener click sulle carte
	private Consumer<Card> cardClickListener = c -> {};

	//unica instanza di AnimationLoop
	private final AnimationLoop animationLoop;


	public GamePanel (){
		super(new BorderLayout(8,8));
		this.animationLoop = new AnimationLoop();
	}

	/**
	 * metodo privato di utilità che inizializza e configura tutti i componenti e "sotto-pannelli"
	 *
	 * @return void
	 */
	public void initGP(List<org.model.Player> players){
		//definisco i pannelli interni
		handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
		scorePanel = new JPanel(new GridLayout(0,1));
		tablePanel = new TablePanel(players,animationLoop);

		//definisco il bottone per la pausa
		pauseButton = new JButton("pausa");
		//in alto la scoreBoard
		JPanel top = new JPanel(new BorderLayout());
		top.add(new JLabel("punteggi: "),BorderLayout.NORTH);
		top.add(scorePanel,BorderLayout.CENTER);
		pauseButton.addActionListener(e->{
			if(onPause!=null){onPause.run();}
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

		add(top, BorderLayout.NORTH);
		add(bottom, BorderLayout.SOUTH);
		add(tablePanel,BorderLayout.CENTER);
	}

	public void setOnPause(Runnable r){
		this.onPause = r;
		}


    public void update(Observable o, Object arg) {
        SwingUtilities.invokeLater(() -> {
            if (arg instanceof ModelEventMessage msg) {
                switch (msg.getEvent()) {
                    case CARDS_DEALT -> handleCardsDealt(msg.getPayload());
                    case CARD_PLAYED -> handleCardPlayed(msg.getPayload());
                    case TURN_STARTED -> handleTurnStarted(msg.getPayload());
                    case TRICK_ENDED -> handleTrickEnded((List<Play>)msg.getPayload());
                    case ROUND_STARTED -> handleRoundStarted();
                    case ROUND_ENDED -> handleRoundEnded((List<Team>) msg.getPayload());
                    case GAME_OVER -> handleGameOver();
					case HAND_UPDATE -> handleHandUpdate((Hand)msg.getPayload());
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
            int idx = players.indexOf(play.getPlayer());
			//DEBUG
			System.out.println("[HANDLE_CARD_PLAYED] player=" + play.getPlayer() + " idx=" + idx + " card=" + play.getCard());
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

	/**
	 * gestisce la fine della "mano" 
	 *
	 * @param plays si aspetta un object List<Play>
	 */
	private void handleTrickEnded(List<Play> plays){
		List<Player> players = new ArrayList<>();

		for(Play play: plays){
			players.add(play.getPlayer());
		}

		tablePanel.clearTablePanel();
	}

    /**
     * Gestisce l'evento ROUND_STARTED.
     */
    private void handleRoundStarted() {
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
    }

	/**
	 * metodo che gestisce la notifica HAND_UPDATE
	 * @param hand si asetta un HAND
	 */
	private void handleHandUpdate(Hand hand){
		updateHand(hand.getCards());
	}
    
	/**
	 * Aggiorna la visuale della mano umana.
	 * @param cards
	 */
	public void updateHand(List<org.model.Card> cards) {
		handPanel.removeAll();
		final int thumbW = 80;
		final int thumbH = 120;
		for (Card c : cards) {
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
	 * chiama i metodi di TablePanel per aggiornare le carte giocate
	 *
	 * @param plays
	 * @param players
	 * @return void
	 */
	public void updateTable(List<Play> plays, List<Player> players) {
		if (tablePanel == null) return;

		// Se la lista è vuota, pulisci subito il tavolo
		if(plays.isEmpty()){
			tablePanel.clearTablePanel();
			return;
		}

		// Altrimenti, mostra le carte giocate
		for (Play p : plays) {
			Player pl = p.getPlayer();
			int idx = players.indexOf(pl);
			if (idx >= 0) {
				tablePanel.playAnimatedCard(idx, p.getCard(),null);
			}
		}
	}
	/**
	 * prova
	 */
	public void playCard(int playerIdx, org.model.Card card){
		if(tablePanel!=null){
			tablePanel.playAnimatedCard(playerIdx, card,()->{
				//DEBUG);
				System.out.println("[HANDLE_CARD_PLAYED CALLBACK] idx=" + playerIdx + " card=" + card.toString());
			});
		} 
	}

	/**
	 * chiama il matodo di TablePanel per evidenziare il giocatore corrente
	 *
	 * @param playerIndex
	 * @return void
	 */
	public void setCurrentPlayer(int playerIndex) {
		if (tablePanel != null) {
			tablePanel.setCurrentPlayer(playerIndex);
		} 
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
	 * getter per l'animation loop
	 *
	 * @return animationLoop
	 */
	public AnimationLoop getAnimationLoop(){
		return this.animationLoop;
	}
}

