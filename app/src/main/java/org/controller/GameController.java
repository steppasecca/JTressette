package org.controller;

import org.view.GamePanel;
import org.view.PauseMenuPanel;
import org.model.TressetteGame;

public class GameController{

	private AppController appController;
	private GamePanel view;
	private PauseMenuPanel pauseMenuPanel;
	private TressetteGame model;

	public GameController(AppController appController){
		
		this.appController = appController;
		this.view = new GamePanel();

		//creo e registro il PausMenuPanel
		this.pauseMenuPanel = new PauseMenuPanel();
		appController.registerOverlay("pause", pauseMenuPanel);

		//setto i callback per i bottoni del pauseMenu
		setPausePanelCallback();
	}

	/**
	 * getter per la view
	 * @return view
	 */
	public GamePanel getView(){return view;}

	/**
	 * getter per il pauseMenuPanel
	 * @return pauseMenuPanel
	 */
	public PauseMenuPanel getPauseMenuPanel(){return pauseMenuPanel;}

	/**
	 * metodo di utilità che setta i callback dei bottoni
	 */
	private void setPausePanelCallback(){

		pauseMenuPanel.setOnResume(() -> appController.hideOverlay());
		pauseMenuPanel.setOnToggleMusic(null);
		pauseMenuPanel.setOnReturnToMenu(()->appController.showMainMenu());
	}

}
