package org.controller;

import org.view.*;
import org.model.*;
import org.util.*;
import javax.swing.*;

/**
 * classe che gestisce organizza e coordina i vari controller e le varie view
 * @author steppasecca
 */
public class AppController{

	private OverlayPanel overlayPanel; 
	private JFrame mainFrame;
	private final MainMenuController mainMenuController;
	private final GameController gameController;
	private final ProfileController profileController;
	private final UserProfileService profileService;
	private final StatController statController;
	private TressetteGame game;

	public  AppController(){

		//inizializzo il frame principale
		initMainFrame();

		//creo il controller del menu principale
		mainMenuController = new MainMenuController(this);

		//creo il model del gioco

		//creo il controller del gioco
		gameController = new GameController(this);

		//inizializzo il profilo
		profileService = new UserProfileService();
		//creo il controller dell profile option
		profileController = new ProfileController(this,profileService.getUserProfile());
		profileController.initProfile();

		//creo lo statController
		statController = new StatController(this,profileService.getUserProfile());

		//mostro il menu principale
		showMainMenu();

	}
	/**
	 * metodo privato di utilità per inizializzare il frame principale
	 */
	private void initMainFrame(){
		this.mainFrame = new JFrame("JTressette");
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainFrame.setLocationRelativeTo(null);
		this.mainFrame.setVisible(true);

		//overlayPanel per mostrare menu "popup"
		overlayPanel = new OverlayPanel();
        mainFrame.setGlassPane(overlayPanel);	
	}

	/**
	 * metodo per mostrare il menu principale
	 */
	public void showMainMenu(){
		mainFrame.setContentPane(mainMenuController.getMainMenuPanel());
		mainFrame.revalidate();
		mainFrame.repaint();
	}

	/**
	 * metodo per mostrare il gamePanel
	 */
	public void showGame(){
		mainFrame.setContentPane(gameController.getView());
	}

	public void initGame() {
		String mode = mainMenuController.getSelectedMode();
		GameModeStrategy strategy;
		if ("2 Giocatori".equals(mode)) {
			strategy = new TwoPlayerStrategy();
		} else {
			strategy = new FourPlayerStrategy();
		}
		gameController.setGame(new TressetteGame(strategy, profileController.getProfile()));
		gameController.initView();
		gameController.startGame();
		showGame();
	}

	/**
	 * metodo per aggiungere un panel all'overlayPanel
	 *
	 * @param name nome assegnato al componente grafico
	 * @param comp componente grafico da mostrare
	 */
    public void registerOverlay(String name, JComponent comp) {
        overlayPanel.addOverlay(name, comp);
    }

	/**
	 * mostra l'overlayPanel
	 * @param name nome del pannello da mostrare
	 */
    public void showOverlay(String name) {
        overlayPanel.showOverlay(name);
    }

	/**
	 * nasconde il panel
	 */
    public void hideOverlay() {
        overlayPanel.hideOverlay();
    }
	
	/**
	 * salva il profilo
	 */
	public void saveProfile(){
		profileService.saveProfile();
	}

	/**
	 * metodo per mostrare lo statPanel
	 */
	public void showStatPanel(){
		statController.showPanel();
	}

}


