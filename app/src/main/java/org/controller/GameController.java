package org.controller;

import org.model.*;
import org.model.events.*;
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
    private final Player humanPlayer; // il player controllato dall'utente (assunto uno solo)

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

        // Set view callback
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
                        // nuove mani: rinfresca completamente
                        refreshView();
                        break;
                    case TURN_STARTED:
                        Integer idx = (Integer) msg.getPayload();
                        if (idx != null) view.setCurrentPlayer(idx);
                        break;
                    case TRICK_ENDED:
                        TrickResult tr = (TrickResult) msg.getPayload();
                        if (tr != null) {
                            // mostra la carta vincente e rinfresca (punteggi, mani ecc.)
                            view.showPlayedCard(tr.getWinnerIndex(), tr.getWinningCard());
                        }
                        refreshView();
                        break;
                    case PROFILE_CHANGED:
                        // payload è UserProfile; se vuoi aggiornare header/menu gestiscilo qui
                        break;
                    case GAME_OVER:
                        handleGameOver();
                        break;
                    default:
                        // altri eventi: refresh per sicurezza
                        refreshView();
                        break;
                }
            } else {
                // backward compatibility (non dovresti arrivare qui dopo il refactor)
                refreshView();
            }

            // dopo ogni aggiornamento prova ad avviare IA se serve
            triggerNextAiIfNeeded();
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

        // aggiorna punteggi
        view.updateScores(game.getTeams());

        // indica il giocatore corrente tramite indice
        view.setCurrentPlayer(game.getCurrentPlayerIndex());
    }

    private synchronized void handleHumanPlay(org.model.Card card) {
        // verifica che sia il turno del giocatore umano
        Player current = game.getPlayers().get(game.getCurrentPlayerIndex());
        if (!current.equals(humanPlayer)) {
            view.appendLog("Non è il tuo turno!");
            return;
        }

        // verifica che la carta sia nella mano
        if (humanPlayer.getHand() == null || !humanPlayer.getHand().containsCard(card)) {
            view.appendLog("Carta non valida.");
            return;
        }

        // fai la giocata: rimuovi carta dalla mano e mettila nel trick
        humanPlayer.playCard(card);
        game.getCurrentTrick().addPlay(humanPlayer, card);

        // prosegui con il turno successivo (model si occuperà di notificare)
        game.nextTurn();
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
                    Thread.sleep(150); // breve pausa per UX
                    return ((ArtificialPlayer) current).chooseCardToPlay(game.getCurrentTrick());
                }

                @Override
                protected void done() {
                    try {
                        org.model.Card card = get();
                        if (card != null && current.getHand().containsCard(card)) {
                            current.playCard(card);
                            game.getCurrentTrick().addPlay(current, card);
                        } else {
                            view.appendLog("IA non ha giocato (carta nulla o non trovata).");
                        }
                        game.nextTurn();
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
