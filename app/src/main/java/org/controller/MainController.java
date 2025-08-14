package org.controller;


import org.model.UserProfile;
import org.view.*;

import javax.swing.*;

import java.awt.Frame;
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
		
		//schermata di gioca
		GamePanel gamePanel = new GamePanel(profile);
		navigator.addScreen(Navigator.Screen.GAME,gamePanel);

		//listener per una nuova partita
		
	}

	

	private void startGame(String mode){
		System.out.println("avvio partita in modalità" + mode);
		navigator.navigate(Navigator.Screen.GAME);
		//istanziare qui TressetteGame
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
