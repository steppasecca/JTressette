package org.util;


import java.io.*;
import java.util.Properties;
import org.model.UserProfile;

public class UserProfileService{
	private UserProfile profile;
	private final File profileFile; 

	public UserProfileService(){
		profileFile = new File("profile.properties");
		initProfile();
	}

	public UserProfile getUserProfile(){return profile;}

	public void initProfile(){
		try {
			profile = loadFromProperties(profileFile);
		} catch (IOException e) {
			profile = new UserProfile("Player");
		}
	}

	public void saveProfile(){
			try {
				saveToProperties(profileFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	/**
	 * metodo di utilità che salva il profile su un file .properies
	 * @param file File
	 * @return void
	 */

	private void saveToProperties(File file) throws IOException{
		Properties props = new Properties();
		props.setProperty("nickname",profile.getNickname());
		props.setProperty("avatarPath", profile.getAvatarPath() == null ? "" : profile.getAvatarPath());
		props.setProperty("gamesPlayed", String.valueOf(profile.getGamesPlayed()));
		props.setProperty("gamesWon", String.valueOf(profile.getGamesWon()));
		try (FileOutputStream fos = new FileOutputStream(file)) {
			props.store(fos, "User Profile");
		}
	}

	/**
	 * metodo di utilità che carica le informazione del profilo da un file .properties
	 * @param file
	 * @return UserProfile
	 */
	private UserProfile loadFromProperties(File file) throws IOException {
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

