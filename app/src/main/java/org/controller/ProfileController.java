package org.controller;

import org.view.ProfileOptionPanel;
import org.model.UserProfile;

/**
 * classe che gestisce il profilo utente
 */

public class ProfileController{

	private ProfileOptionPanel view;
	private UserProfile model;
	private AppController appController;

	public ProfileController(AppController appController){
		this.appController = appController;
	}

	public void initProfile(){
	}

	public ProfileOptionPanel getView(){return view;}

}
