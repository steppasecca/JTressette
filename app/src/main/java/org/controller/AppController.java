package org.controller;

import org.model.*;
import org.view.*;
import javax.swing.*;

/**
 * classe che gestisce e organizza i vari controller e le varie view
 * @author steppasecca
 */
public class AppController{

	private JFrame mainFrame;
	private final MainMenuController mainMenuController;
	private final GameController gameController;
	private final ProfileController profileController;

	public  AppController(){

		//inizializzo il frame principale
		initMainFrame();

		//creo il controller del menu principale
		mainMenuController = new MainMenuController(this);

		//creo il controller del gioco
		gameController = new GameController(this);

		//creo il controller dell profile option
		profileController = new ProfileController(this);

	}
	/**
	 * metodo privato di utilità per inizializzare il frame principale
	 */
	private void initMainFrame(){
		this.mainFrame = new JFrame("JTressette");
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainFrame.setLocationRelativeTo(null);
		this.mainFrame.setVisible(true);
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

	/**
	 * metodo per mostrare il profileOptionPanel
	 */
	public void showOptionProfile(){
		mainFrame.setContentPane(profileController.getView());
	}

	
}


