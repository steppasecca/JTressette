package org.controller;

import org.view.GamePanel;
import org.model.TressetteGame;

public class GameController{

	private AppController appController;
	private GamePanel view;
	private TressetteGame model;

	public GameController(AppController appController){
		this.appController = appController;
	}

	/**
	 * getter per la view
	 * @return view
	 */
	public GamePanel getView(){return view;}
}
