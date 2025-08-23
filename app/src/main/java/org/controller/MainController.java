package org.controller;


import org.model.*;
import org.view.*;

import javax.swing.*;

import java.io.File;
import java.io.IOException;

/**
 * controller principale coordina il menu,il gioco e le opzioni
 */

public class MainController {

	private final UserProfile profile;
	private final File saveFile = new File("profile.properties");
	private final Navigator navigator;

	public MainController(){

		//carico o creo il profilo
		profile = loadProfile();

		//creo la finestra principale
		JFrame frame = new JFrame("JTressette");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600,400);
		frame.setLocationRelativeTo(null);

		navigator = new Navigator(frame);

		//inizializzo le schermate

		initScreens();

		//mostro il menu iniziale
		navigator.navigate(Navigator.Screen.MENU);

		frame.setVisible(true);
	}

	private void initScreens() {
		MainMenuPanel menuPanel = new MainMenuPanel(profile);
		navigator.addScreen(Navigator.Screen.MENU,menuPanel);

		//listener per "menu delle opzioni"
		menuPanel.getOptionsButton().addActionListener( e -> {
			OptionsDialog dialog = new OptionsDialog(profile);
			dialog.setVisible(true);
			saveProfile();
		});
		menuPanel.getStartButton().addActionListener(e -> {
			String mode = menuPanel.getSelectedMode();
			startGame(mode);
		});	
		
	}

	

	private void startGame(String mode){
		System.out.println("avvio partita in modalità " + mode);

		GameModeStrategy strategy = mode.equals("2 Giocatori") ?
			new TwoPlayerStrategy() : new FourPlayerStrategy();

		TressetteGame game = new TressetteGame(strategy,profile);
		game.startGame(); // mescola e distribuisce

		// trova il player umano (primo HumanPlayer in lista)
		Player human = game.getPlayers().stream()
			.filter(p -> p instanceof org.model.HumanPlayer)
			.findFirst()
			.orElse(game.getPlayers().get(0)); // fallback

		GamePanel gamePanel = new GamePanel();
		navigator.addScreen(Navigator.Screen.GAME, gamePanel);

		// crea TablePanel e impostalo nella view PRIMA di creare il controller
		TablePanel tp = new TablePanel(game.getPlayers());
		gamePanel.setTablePanel(tp);		
		// crea controller che collegherà modello e view
		new GameController(game, gamePanel, navigator, human);

		navigator.navigate(Navigator.Screen.GAME);
	}
	private UserProfile loadProfile(){
		try {
			return UserProfile.loadFromProperties(saveFile);
		} catch (IOException e){
			return new UserProfile("Player");
		}
	}

	private void saveProfile() {
		try {
			profile.saveToProperties(saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
