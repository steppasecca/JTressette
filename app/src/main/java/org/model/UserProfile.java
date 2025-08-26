package org.model;

import java.io.*;
import java.util.Properties;

import org.events.*;
import java.util.Observable;

/**
 * modella il profilo di un utente
 */

public class UserProfile extends Observable{

	private String nickname;
	private String avatarPath;
	private int gamesWon;
	private int gamesPlayed;

	public UserProfile(String nickname){

		this.nickname = nickname;
		this.avatarPath = "";
		this.gamesPlayed = 0;
		this.gamesWon = 0;
	}	

	//metodi getter e setter

	public String getNickname(){return this.nickname;}
	public String getAvatarPath(){return avatarPath;}
	public int getGamesPlayed(){return this.gamesPlayed;}
	public int getGamesWon() {return this.gamesWon;}

	/**
	 * setter per il nickname
	 * @param nickname
	 * @return void
	 */
	public void setNickname(String nickname){
		this.nickname = nickname;
		setChanged();
		notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.PROFILE_CHANGED, this));
	}

	/**
	 * setter per l'avatar
	 *
	 * @param avatarPath
	 * @return void
	 */
	public void setAvatarPath(String avatarPath){
		this.avatarPath = avatarPath;
		setChanged();
		notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.PROFILE_CHANGED, this));
	}

	/**
	 * metodo che aggiorna le statistiche
	 * @param won boolean
	 * @return void
	 */

	public void addGame(boolean won){
		this.gamesPlayed++;
		if(won){gamesWon++;}
		setChanged();
		notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.PROFILE_CHANGED, this));
	
	}

	/**
	 * metodo che salva il profile su un file
	 *
	 * @param file File
	 * @return void
	 */

	public void saveToProperties(File file) throws IOException{
		Properties props = new Properties();
		props.setProperty("nickname",nickname);
		props.setProperty("avatarPath", avatarPath == null ? "" : avatarPath);
		props.setProperty("gamesPlayed", String.valueOf(gamesPlayed));
		props.setProperty("gamesWon", String.valueOf(gamesWon));
		try (FileOutputStream fos = new FileOutputStream(file)) {
			props.store(fos, "User Profile");
		}
	}

	public static UserProfile loadFromProperties(File file) throws IOException {
		if (!file.exists()) {
			return new UserProfile("Player");
		}

		Properties props = new Properties();
		try (FileInputStream fis = new FileInputStream(file)) {
			props.load(fis);
		}

		UserProfile profile = new UserProfile(props.getProperty("nickname", "Player"));
		profile.avatarPath = props.getProperty("avatarPath", "");

		try {
			profile.gamesPlayed = Integer.parseInt(props.getProperty("gamesPlayed", "0"));
		} catch (NumberFormatException e) {
			profile.gamesPlayed = 0;
		}

		try {
			profile.gamesWon = Integer.parseInt(props.getProperty("gamesWon", "0"));
		} catch (NumberFormatException e) {
			profile.gamesWon = 0;
		}

		return profile;
	}

}
