package org.model;


/**
 * modella il profilo di un utente
 */

public class UserProfile{

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
	}

	/**
	 * setter per l'avatar
	 *
	 * @param avatarPath
	 * @return void
	 */
	public void setAvatarPath(String avatarPath){
		this.avatarPath = avatarPath;
	}

	/**
	 * metodo che aggiorna le statistiche
	 * @param won boolean
	 * @return void
	 */
	public void addGame(boolean won){
		this.gamesPlayed++;
		if(won){gamesWon++;}
	}

	/**
	 *setter per il gamesWon 
	 * @param gamesWon
	 */
	public void setGamesWon(int gamesWon){
		this.gamesWon = gamesWon;
	}

	/**
	 * setter per i gamesPlayed
	 * @param gamesPlayed
	 */
	public void setGamesPlayed(int gamesPlayed){
		this.gamesPlayed = gamesPlayed;

	}

}
