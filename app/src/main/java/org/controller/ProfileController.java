package org.controller;

import org.view.ProfileMenuPanel;
import org.model.UserProfile;

/**
 * classe che gestisce il profilo utente
 */

public class ProfileController{

	private ProfileMenuPanel view;
	private UserProfile profile;
	private AppController appController;

	public ProfileController(AppController appController,UserProfile profile){
		this.appController = appController;
		this.profile = profile;
	}

	public void initProfile(){

		// Inizializza la View con i dati del Model
		view = new ProfileMenuPanel(profile);
		appController.registerOverlay("profile", view);

		// Collega i callback
		view.setOnSave(() -> {
			profile.setNickname(view.getNicknameInput());
			profile.setAvatarPath(view.getSelectedAvatarPath());
			appController.saveProfile();	
			appController.hideOverlay();
		});

		view.setOnCancel(() -> {
			appController.hideOverlay();
		});
	}

	public ProfileMenuPanel getView(){return view;}

}
