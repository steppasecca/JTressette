package org.controller;

import org.model.*;
import org.view.*;
import javax.swing.*;

/**
 * controller che coordina e gestisce quello che riguarda il menu iniziale
 */
public class MainMenuController{

	private AppController appController;
	private MainMenuPanel view;

	public MainMenuController(AppController appController){
		this.appController = appController;
		this.view = new MainMenuPanel();

		view.setOnStart(() -> appController.initGame());
		view.setOnToggleOption(()-> appController.showOverlay("profile"));
		view.setOnToggleStat(() -> appController.showStatPanel());
	}

	/**
	 * getter per la view MainPanel
	 */
	public MainMenuPanel getMainMenuPanel(){
		return view;
	}

	public String getSelectedMode() {
		return view.getSelectedMode();
	}

}
