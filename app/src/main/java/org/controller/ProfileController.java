package org.controller;

import org.view.ProfileMenuPanel;
import org.model.UserProfile;
import java.io.*;
import java.util.Properties;

/**
 * classe che gestisce il profilo utente
 */

public class ProfileController{

	private ProfileMenuPanel view;
	private UserProfile model;
	private AppController appController;

	public ProfileController(AppController appController){
		this.appController = appController;
	}

	public void initProfile(){
	}

	public ProfileMenuPanel getView(){return view;}

	/**
	 * metodo che salva il profile su un file
	 *
	 * @param file File
	 * @return void
	 */

	public void saveToProperties(File file) throws IOException{
		Properties props = new Properties();
		props.setProperty("nickname",model.getNickname());
		props.setProperty("avatarPath", model.getAvatarPath() == null ? "" : model.getAvatarPath());
		props.setProperty("gamesPlayed", String.valueOf(model.getGamesPlayed()));
		props.setProperty("gamesWon", String.valueOf(model.getGamesWon()));
		try (FileOutputStream fos = new FileOutputStream(file)) {
			props.store(fos, "User Profile");
		}
	}

	public UserProfile loadFromProperties(File file) throws IOException {
		if (!file.exists()) {
			return new UserProfile("Player");
		}

		Properties props = new Properties();
		try (FileInputStream fis = new FileInputStream(file)) {
			props.load(fis);
		}

		UserProfile profile = new UserProfile(props.getProperty("nickname", "Player"));
		profile.setAvatarPath(props.getProperty("avatarPath", ""));

		try {
			profile.setGamesPlayed(Integer.parseInt(props.getProperty("gamesPlayed", "0")));
		} catch (NumberFormatException e) {
			profile.setGamesPlayed(0);
		}

		try {
			profile.setGamesWon(Integer.parseInt(props.getProperty("gamesWon", "0")));
		} catch (NumberFormatException e) {
			profile.setGamesWon(0);
		}

		return profile;
	}

}
