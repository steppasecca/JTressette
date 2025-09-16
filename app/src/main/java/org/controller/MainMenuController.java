package org.controller;

import org.model.*;
import org.view.*;
import javax.swing.*;

public class MainMenuController{

	private AppController appController;
	private MainMenuPanel view;

	public MainMenuController(AppController appController){
		this.appController = appController;
		this.view = new MainMenuPanel();

		view.setOnStart(() -> appController.showGame());
		view.setOnToggleOption(()-> appController.showOptionProfile());
	}

	/**
	 * getter per la view MainPanel
	 */
	public MainMenuPanel getMainMenuPanel(){
		return view;
	}


}
