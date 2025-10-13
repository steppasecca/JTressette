package org.controller;

import org.view.GamePanel;
import org.view.PauseMenuPanel;
import org.model.*;

public class GameController {

    private AppController appController;
    private GamePanel view;
    private PauseMenuPanel pauseMenuPanel;
    private TressetteGame model;

    public GameController(AppController appController) {
        this.appController = appController;
        this.view = new GamePanel();

        this.pauseMenuPanel = new PauseMenuPanel();
        appController.registerOverlay("pause", pauseMenuPanel);
        setPausePanelCallback();
    }

    public GamePanel getView() {
        return view;
    }

    public PauseMenuPanel getPauseMenuPanel() {
        return pauseMenuPanel;
    }

    private void setPausePanelCallback() {
        pauseMenuPanel.setOnResume(() -> appController.hideOverlay());
        pauseMenuPanel.setOnToggleMusic(null);
        pauseMenuPanel.setOnReturnToMenu(() -> {
            // IMPORTANTE: Ferma l'animation loop quando si torna al menu
            stopGame();
            appController.hideOverlay();
            appController.showMainMenu();
        });
    }

    public void setGame(TressetteGame game) {
        if (game != null) {
            this.model = game;
        }
    }

    public void initView() {
        if (model != null) {
            view.initGP(model.getPlayers());

            // Avvia l'animation loop
            view.getAnimationLoop().start();

            // Configura il listener per le carte del giocatore umano
            view.setCardClickListener(card -> {
                Player humanPlayer = model.getPlayers().stream()
                    .filter(p -> p instanceof HumanPlayer)
                    .findFirst()
                    .orElse(null);

                if (humanPlayer != null && model.getCurrentPlayer().equals(humanPlayer)) {
                    Play play = new Play(humanPlayer, card);
                    if (model.isValidPlay(play)) {
                        model.playCard(play);
                    } else {
                        System.out.println("giocata non valida");
                    }
                }
            });
            
            model.addObserver(view);
        }

        view.setOnPause(() -> appController.showOverlay("pause"));
    }

    public void startGame() {
        if (model != null) {
            model.startGame();
        }
    }

    /**
     * Ferma il gioco e l'animation loop
     */
    public void stopGame() {
        if (view != null && view.getAnimationLoop() != null) {
            view.getAnimationLoop().stop();
        }
    }

    /**
     * Pulisce tutto prima di distruggere il controller
     */
    public void cleanupAndStop() {
        stopGame();
        if (model != null) {
            model.deleteObservers();
        }
    }
}
