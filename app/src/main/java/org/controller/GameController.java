package org.controller;

import org.model.*;
import org.events.*;
import org.view.GamePanel;
import org.view.TablePanel;

import javax.swing.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Collections;

/**
 * GameController: connette il TressetteGame (model) con la GamePanel (view).
 */
public class GameController implements Observer {

    private final TressetteGame game;
    private final GamePanel view;
    private final Navigator navigator;
    private final Player humanPlayer; // il player controllato dall'utente 

    public GameController(TressetteGame game, GamePanel view, Navigator navigator, Player humanPlayer) {
        this.game = game;
        this.view = view;
        this.navigator = navigator;
        this.humanPlayer = humanPlayer;

        // crea e imposta il TablePanel nella view (tavolo parametrico 2/4)
        TablePanel tp = new TablePanel(game.getPlayers());
        this.view.setTablePanel(tp);

        // Register as observer 
        this.game.addObserver(this);

        // listener per il click sulla carta che invoca handleHumanPlay
        this.view.setCardClickListener(card -> handleHumanPlay(card));

        // inizializza view con stato corrente
        refreshView();

        // se l'IA inizia per prima, attivala
        triggerNextAiIfNeeded();
    }

    // Observer callback dal modello
    @Override
    public void update(Observable o, Object arg) {
        SwingUtilities.invokeLater(() -> {
            // se riceviamo ModelEventMessage, gestiscilo; altrimenti refresh generale
            if (arg instanceof ModelEventMessage) {
                ModelEventMessage msg = (ModelEventMessage) arg;
                switch (msg.getEvent()) {
                    case CARDS_DEALT:
                        refreshView();
						//aggiungere effetto tipo carte distribuite evviva
                        break;
					case CARD_PLAYED:
						Play play =(Play) msg.getPayload();
						view.appendLog("Il giocatore " + play.getPlayer().getNome() 
								+ " ha giocato il " + play.getCard().toString());
						refreshView();
                    case TURN_STARTED:
						refreshView();
						triggerNextAiIfNeeded();
                        break;
                    case TRICK_ENDED:
						Player winnerOfTheTrick = (Player)msg.getPayload();
						view.appendLog("Ha preso questa mano: "+ winnerOfTheTrick.getNome() 
								+"della squadra: "  + winnerOfTheTrick.getTeam().getTeamName());
                        refreshView();
                        break;
					case ROUND_STARTED:
						refreshView();
						break;
					case ROUND_ENDED:
						List<Team> teams = (List<Team>) msg.getPayload();
						view.updateScores(teams); 
						JOptionPane.showMessageDialog(view, "Fine della round! Si procede al prossimo.");
						refreshView();
						break;
                    case PROFILE_CHANGED:
                        break;
                    case GAME_OVER:
                        handleGameOver();
                        break;
                    default:
                        refreshView();
                        break;
                }
            } else {
                // backward compatibility (non dovresti arrivare qui dopo il refactor)
                refreshView();
            }
        });
    }

    /** 
	 * Aggiorna la view leggendo lo stato dal model 
	 */
    private void refreshView() {
        // aggiorna mano umana
        if (humanPlayer.getHand() != null) {
            view.updateHand(humanPlayer.getHand().getCards());
        } else {
            view.updateHand(Collections.emptyList());
        }

        // aggiorna tavolo: passa le giocate correnti e la lista di giocatori (per mappare player->indice)
        Trick trick = game.getCurrentTrick();
        List<Play> plays = trick.getPlays();
        view.updateTable(plays, game.getPlayers());

        // indica il giocatore corrente tramite indice
        view.setCurrentPlayer(game.getCurrentPlayerIndex());
    }

    private synchronized void handleHumanPlay(org.model.Card card) {
        // verifica che sia il turno del giocatore umano
		Play play = new Play(humanPlayer,card);

		if(!game.playCard(play)){
			view.appendLog("Giocata non valida");
		}
    }

    /**
     * Se il giocatore corrente è un'IA, avvia la loro mossa in background.
     */
    private void triggerNextAiIfNeeded() {
        if (game.isRoundOver() || game.isGameOver()) return;

        Player current = game.getPlayers().get(game.getCurrentPlayerIndex());
        if (current instanceof ArtificialPlayer) {
            new SwingWorker<org.model.Card, Void>() {
                @Override
                protected org.model.Card doInBackground() throws Exception {
                    Thread.sleep(250); // breve pausa per dare un senso realistico
                    return ((ArtificialPlayer) current).chooseCardToPlay(game.getCurrentTrick());
                }

                @Override
                protected void done() {
                    try {
						Card card = get();
						if(card!=null){
							Play play = new Play(current, card);
							if (!game.playCard(play)) {
								view.appendLog("IA ha tentato una giocata non valida!");
							}
						}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        }
    }

    private void handleGameOver() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Partita terminata!");
            navigator.navigate(Navigator.Screen.MENU);
        });
    }
}
