package org.controller;

import org.view.GamePanel;
import org.view.PauseMenuPanel;
import org.model.*;
/**
 * controller che coordina e gestisce TressetteGame(model) e GamePanel(view)
 */
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
		pauseMenuPanel.setOnReturnToMenu(()->{
			appController.hideOverlay();
			appController.showMainMenu();
		});
	}


	public void setGame(TressetteGame game){
		if(game != null){
			this.model = game;
		}
	}

	public void initView(){
		if(model!=null){
			view.initGP(model.getPlayers());

			view.getAnimationLoop().start();
			// Configura il listener per le carte del giocatore umano
			view.setCardClickListener(card -> {
				Player humanPlayer = model.getPlayers().stream()
					.filter(p -> p instanceof HumanPlayer)
					.findFirst()
					.orElse(null);

				if (humanPlayer != null && model.getCurrentPlayer().equals(humanPlayer)) {
					Play play = new Play(humanPlayer, card);
					if(model.isValidPlay(play)){
						model.playCard(play);
					}
					else{
						System.out.println("giocata non valida");
					}
				}
			});
			model.addObserver(view);
		}

		view.setOnPause(()->appController.showOverlay("pause"));
	}
	public void startGame() {
		if (model != null) {
			model.startGame();
		}
	}
}
