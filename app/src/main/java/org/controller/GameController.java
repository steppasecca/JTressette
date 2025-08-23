package org.controller;

import org.model.events.*;
import org.model.*;
import org.view.GamePanel;

import javax.swing.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * GameController: connette il TressetteGame (model) con la GamePanel (view).
 * Rispetta MVC: la view notifica l'input utente, il controller occupa la logica di interazione.
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

        // Register as observer (usando Observable/Observer come richiesto dalla specifica)
        this.game.addObserver(this);

        // Set view callback
        this.view.setCardClickListener(card -> {
            handleHumanPlay(card);
        });

        // inizializza view con stato corrente
        refreshView();
        // se l'IA inizia per prima, attivala
        triggerNextAiIfNeeded();
    }

    // Observer callback dal modello
    @Override
    public void update(Observable o, Object arg) {
        // arg è la stringa di notifica che tu invii dal modello (es: "turnEnded")
        SwingUtilities.invokeLater(() -> {
            // aggiorna la view dallo stato corrente del modello
            refreshView();

			if (!(arg instanceof org.model.events.ModelEventMessage)) return;
			ModelEventMessage msg = (ModelEventMessage) arg;
			switch (msg.getEvent()) {
				case CARDS_DEALT:
					// aggiorna mano/GUI partendo da game.getPlayers()
					refreshViewFromModel();
					break;
				case TURN_STARTED:
					Integer idx = (Integer) msg.getPayload();
					view.setCurrentPlayer(idx);
					break;
				case TRICK_ENDED:
					org.model.events.TrickResult tr = (org.model.events.TrickResult) msg.getPayload();
					// animazioni, aggiornamento punteggi
					refreshViewFromModel();
					break;
				case PROFILE_CHANGED:
					UserProfile p = (UserProfile) msg.getPayload();
					// aggiorna header/profile view
					break;
					// ...
				default:
					break;
				}
            // dopo ogni aggiornamento prova ad avviare IA se serve
            triggerNextAiIfNeeded();
        });
    }
    private  void setTablePanel(TablePanel tp) {
    if (tablePanelComponent != null) remove(tablePanelComponent);
    tablePanelComponent = tp;
    add(tablePanelComponent, BorderLayout.CENTER); // sostituisce il precedente panel central
    revalidate();
    repaint();
}

// aggiornamento del giocatore corrente chiamato dal controller osservatore:
public void setCurrentPlayer(int playerIndex) {
    if (tablePanelComponent != null) {
        tablePanelComponent.setCurrentPlayer(playerIndex);
    } else {
        appendLog("tablePanel non inizializzato");
    }
}oid refreshView() {
        // aggiorna mano umana
        if (humanPlayer.getHand() != null) {
            view.updateHand(humanPlayer.getHand().getCards());
        } else {
            view.updateHand(java.util.Collections.emptyList());
        }

        // aggiorna tavolo
        Trick trick = game.getCurrentTrick();
        // suppongo che Trick esponga List<Play> getPlays() nella versione fixata
        List<Play> plays = null;
        try {
            plays = (List<Play>) (Object) trick.getPlays(); // cast sicuro se Trick ha getPlays()
        } catch (Exception e) {
            // in caso la tua implementazione abbia un nome diverso, prova a prendere le carte come fallback
            plays = java.util.Collections.emptyList();
        }
        view.updateTable(plays);

        // aggiorna punteggi
        view.updateScores(game.getTeams());

        // indica il giocatore corrente
        Player current = game.getPlayers().get(game.getCurrentPlayerIndex());
        view.setCurrentPlayer(current.getNome());
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

        // notifica modello (se necessario) e procedi con nextTurn
        game.nextTurn();
    }

   /**
     * Se il giocatore corrente è un'IA, avvia la loro mossa in background.
     * Usa SwingWorker per non bloccare l'EDT. Dopo la mossa chiama game.nextTurn() dalla done().
     */
    private void triggerNextAiIfNeeded() {
        if (game.isRoundOver() || game.isGameOver()) return;

        Player current = game.getPlayers().get(game.getCurrentPlayerIndex());
        if (current instanceof ArtificialPlayer) {
            // Esegue una sola mossa IA per volta
            new SwingWorker<org.model.Card, Void>() {
                @Override
                protected org.model.Card doInBackground() throws Exception {
                    // l'IA sceglie la carta (mettere sleep breve se vuoi simulare pensiero)
                    Thread.sleep(150); // piccola pausa per effetto UX
                    return ((ArtificialPlayer) current).chooseCardToPlay(game.getCurrentTrick());
                }

                protected void done() {
                    try {
                        org.model.Card card = get();
                        if (card != null && current.getHand().containsCard(card)) {
                            current.playCard(card);
                            game.getCurrentTrick().addPlay(current, card);
                        } else {
                            // fallback: se IA non ha carta valida, log
                            view.appendLog("IA non ha giocato (carta nulla o non trovata).");
                        }
                        // Muovi il turno avanti (model decide se fine presa ecc.)
                        game.nextTurn();
                        // il modello notificherà gli observer e triggerNextAiIfNeeded verrà richiamato di nuovo
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
        }
    }
    private void handleGameOver() {
        // mostra semplice dialog e torna al menu
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Partita terminata!");
            navigator.navigate(Navigator.Screen.MENU);
        });
    }
}
