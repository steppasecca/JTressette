
package org.controller;

import org.model.*;
import org.util.*;
import org.view.*;

import javax.swing.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Collections;

/**
 * GameController: connette il TressetteGame (model) con la GamePanel (view).
 */
public class GameController {

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

		//setto un riferimento nella view al controller
		this.view.setController(this);

		this.game.addObserver(view);

        // listener per il click sulla carta che invoca handleHumanPlay
        this.view.setCardClickListener(card -> handleHumanPlay(card));

		//collega i bottoni ai listener
		handlePauseMenu();
        // inizializza view con stato corrente

    }
    /**
     * Metodo pubblico chiamato dalla Vista quando rileva un cambio di turno
     * o la fine di una mano, per verificare se un'IA deve giocare.
     */
    public void checkAiTurn() {
        if (game.isRoundOver() || game.isGameOver()) return;

        Player current = game.getCurrentPlayer();

        if (current instanceof ArtificialPlayer) {
            // Usa SwingWorker per non bloccare l'interfaccia utente
            new SwingWorker<Card, Void>() {
                @Override
                protected Card doInBackground() throws Exception {
                    Thread.sleep(250); // Breve pausa per dare un senso realistico
                    return ((ArtificialPlayer) current).chooseCardToPlay(game.getCurrentTrick());
                }

                @Override
                protected void done() {
                    try {
                        Card card = get();
                        if (card != null && game.getCurrentPlayer() == current) {
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


    private synchronized void handleHumanPlay(org.model.Card card) {
        // verifica che sia il turno del giocatore umano
        Play play = new Play(humanPlayer, card);

        if(!game.playCard(play)){
            view.appendLog("Giocata non valida");
        }
    }

	private void handlePauseMenu(){
		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(view);
		if (frame == null) {
			// fallback: se il frame non è ancora pronto, rinvia
			SwingUtilities.invokeLater(this::handlePauseMenu);
			return;
		}

		PauseMenuPanel pauseMenu = (PauseMenuPanel) frame.getGlassPane();

		pauseMenu.setOnResume(() -> pauseMenu.setVisible(false));

		pauseMenu.setOnToggleMusic(() -> {
			if (AudioManager.getInstance().isPlaying()) {
				AudioManager.getInstance().stopBackground();
				view.appendLog("Musica spenta");
			} else {
				AudioManager.getInstance().playBackground("musica.wav"); // usa il tuo file
				view.appendLog("Musica accesa");
			}
		});

		pauseMenu.setOnReturnToMenu(() -> {
			pauseMenu.setVisible(false);
			navigator.navigate(Navigator.Screen.MENU);
		});
	}

    /**
     * Se il giocatore corrente è un'IA, avvia la loro mossa in background.
     */

    private void handleGameOver() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Partita terminata!");
            navigator.navigate(Navigator.Screen.MENU);
        });
    }
}

