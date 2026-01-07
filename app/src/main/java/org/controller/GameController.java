package org.controller;

import org.view.GamePanel;
import org.view.PauseMenuPanel;
import org.model.*;
import org.util.*; 

public class GameController {

    private AppController appController;
    private GamePanel view;
    private PauseMenuPanel pauseMenuPanel;
    private TressetteGame model;
    private boolean musicStarted;

    // Flag per bloccare click multipli
    private volatile boolean isWaitingForTurn = false;

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
        pauseMenuPanel.setOnToggleMusic(() -> {
            AudioManager audio = AudioManager.getInstance();
            if (!musicStarted) {
                audio.playBackground("music.wav");
                musicStarted = true;
                pauseMenuPanel.setMusicButtonText("Spegni Musica");
            } else {
                if (audio.isPlaying()) {
                    audio.pauseBackground();
                } else {
                    audio.resumeBackground();
                }
            }
            pauseMenuPanel.updateMusicButtonText();
        });
        pauseMenuPanel.setOnReturnToMenu(() -> {
            //ferma l'animation loop quando si torna al menu
            stopGame();
            cleanupAndStop();
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
            // ignora il cliclk se stiamo giá giocando
            if (isWaitingForTurn) {
                System.out.println("[GameController] Click ignorato: elaborazione in corso");
                return;
            }
            
            Player humanPlayer = model.getPlayers().stream()
                .filter(p -> p instanceof HumanPlayer)
                .findFirst()
                .orElse(null);

            if (humanPlayer != null && model.getCurrentPlayer().equals(humanPlayer)) {
                Play play = new Play(humanPlayer, card);
                if (model.isValidPlay(play)) {
                    // Imposta il flag prima di giocare
                    isWaitingForTurn = true;
                    System.out.println("[GameController] Flag impostato: blocco click");
                    model.playCard(play);
                } else {
                    System.out.println("giocata non valida");
                }
            }
        });
        
        model.addObserver(view);
        
        // Aggiungo un observer per resettare il flag SOLO quando è il turno dell'umano
        model.addObserver((o, arg) -> {
            if (arg instanceof ModelEventMessage msg) {
                if (msg.getEvent() == ModelEventMessage.ModelEvent.TURN_STARTED) {
                    Integer playerIndex = (Integer) msg.getPayload();
                    Player currentPlayer = model.getPlayers().get(playerIndex);
                    
                    // Resetto il flag solo se è il turno del giocatore umano
                    if (currentPlayer instanceof HumanPlayer) {
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            isWaitingForTurn = false;
                            System.out.println("[GameController] Flag resettato: turno umano");
                        });
                    } else {
                        System.out.println("[GameController] Flag mantenuto: turno AI");
                    }
                }
            }
        });
    }

    view.setOnPause(() -> appController.showOverlay("pause"));
}

    public void startGame() {
        if (model != null) {
            model.startGame();
        //AudioManager.getInstance().playBackground("music.wav");
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
        // Ferma l'animation loop
        stopGame();
        
        // Pulisce il model (ferma timer, rimuove observer)
        if (model != null) {
            model.cleanup();
            model = null; // Rilascia il riferimento
        }
        
        //ricrea una view pulita
        view = new GamePanel();
    }
}
