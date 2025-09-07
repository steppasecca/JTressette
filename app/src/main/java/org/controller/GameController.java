
package org.controller;

import org.model.*;
import org.util.*;
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
                        break;

                    case CARD_PLAYED: {
                        Play play = (Play) msg.getPayload();
                        view.appendLog("Il giocatore " + play.getPlayer().getNome() 
                                + " ha giocato il " + play.getCard().toString());
						int idx = game.getPlayers().indexOf(play.getPlayer());
						view.playCard(idx,play.getCard());
                        refreshView();
                        break;
                    }

                    case TURN_STARTED: {
                        // il payload dovrebbe essere l'indice del giocatore corrente (Integer) oppure null (fallback)
                        Object p = msg.getPayload();
                        if (p instanceof Integer) {
                            int idx = (Integer) p;
                            view.setCurrentPlayer(idx);
                            view.appendLog("TURN_STARTED payload indice: " + idx 
                                + " (" + game.getPlayers().get(idx).getNome() + ")");
                        } else {
                            // fallback: usa lo stato del model
                            view.appendLog("TURN_STARTED (no payload) currentIndex=" + game.getCurrentPlayerIndex());
                            view.setCurrentPlayer(game.getCurrentPlayerIndex());
                        }
                        refreshView();
                        triggerNextAiIfNeeded();
                        break;
                    }

                    case TRICK_ENDED: {
                        Object payload = msg.getPayload();
                        Player winner = null;
                        List<Play> playsForView = Collections.emptyList();

                        // gestisce il nuovo payload Object[] {winner, playsSnapshot}
                        if (payload instanceof Object[]) {
                            Object[] arr = (Object[]) payload;
                            if (arr.length >= 1 && arr[0] instanceof Player) {
                                winner = (Player) arr[0];
                            }
                            if (arr.length >= 2 && arr[1] instanceof List) {
                                playsForView = (List<Play>) arr[1];
                            }
                        } else if (payload instanceof Player) { //else if di sicurezza se si torna un payload con solo i player
                            winner = (Player) payload;
                        }

                        if (winner != null) {
                            view.appendLog("Ha preso questa mano: " + winner.getNome() 
                                + " della squadra: " + winner.getTeam().getTeamName());
                        } else {
                            view.appendLog("Ha preso questa mano: (nessun winner nel payload)");
                        }

                        // mostra subito le carte della mano (snapshot)
                        view.updateTable(playsForView, game.getPlayers());

                        // tienile a video per 1 secondo, poi pulisci e lascia partire il nuovo turno
                        new javax.swing.Timer(2000, e -> {
                            ((javax.swing.Timer) e.getSource()).stop();
                            view.updateTable(Collections.emptyList(), game.getPlayers());
                            refreshView();
                            // dopo la pulizia triggeriamo l'IA se tocca a lei
                            triggerNextAiIfNeeded();
                        }) {{
                            setRepeats(false);
                            start();
                        }};
                        break;
                    }

                    case ROUND_STARTED:
                        refreshView();
                        break;

                    case ROUND_ENDED: {
                        List<Team> teams = (List<Team>) msg.getPayload();
                        view.updateScores(teams); 
                        JOptionPane.showMessageDialog(view, "Fine della round! Si procede al prossimo.");
                        refreshView();
                        break;
                    }

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
        Play play = new Play(humanPlayer, card);

        if(!game.playCard(play)){
            view.appendLog("Giocata non valida");
        }
    }

    /**
     * Se il giocatore corrente è un'IA, avvia la loro mossa in background.
     */
    private void triggerNextAiIfNeeded() {
        if (game.isRoundOver() || game.isGameOver()) return;

        int idx = game.getCurrentPlayerIndex();
        Player current = game.getPlayers().get(idx);

        // debug log: utile per capire perché l'IA non parte
        view.appendLog("[DEBUG] triggerNextAiIfNeeded: currentIndex=" + idx + ", player=" 
            + current.getNome() + " (" + current.getClass().getSimpleName() + ")");

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
                        if (card != null) {
                            // controllo per evitare che tenti di giocare anche quando non è il suo turno
                            if (game.getPlayers().get(game.getCurrentPlayerIndex()) != current) {
                                view.appendLog("[DEBUG] IA scelta scartata: non è più il turno (index changed).");
                                return;
                            }
                            Play play = new Play(current, card);
                            if (!game.playCard(play)) {
                                view.appendLog("IA ha tentato una giocata non valida!");
                            } else {
                                view.appendLog("[DEBUG] IA ha giocato: " + card);
                            }
                        } else {
                            view.appendLog("[DEBUG] IA non ha trovato carta da giocare (chooseCardToPlay returned null).");
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

