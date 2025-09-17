package org.controller;

import org.model.UserProfile;
import org.view.StatPanel;

public class StatController {

    private final AppController appController;
    private final UserProfile profile;
    private final StatPanel view;

    public StatController(AppController appController, UserProfile profile) {
        this.appController = appController;
        this.profile = profile;
        this.view = new StatPanel();
        
        // Registra il pannello nell'overlay
        appController.registerOverlay("stats", view);

		view.setOnExit(()->appController.hideOverlay());
    }

    /**
     * Aggiorna la vista con i dati correnti del profilo e la mostra.
     */
    public void showPanel() {
        //recupera i dati aggiornati e aggiorna la view(non mi fa impazzire questa soluzione
        view.updateStats(profile);
        //Mostra la view
        appController.showOverlay("stats");
    }

}
