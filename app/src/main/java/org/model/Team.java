package org.model;

import java.util.ArrayList;
import java.util.List;
/**
 * classe che rappresenta una squadra
 *
 * @author steppasecca
 */

public class Team{

	private String teamName;
	private List<Player> players;
	private CapturedCards capturedCards;
	private int gamesWon;
	private int gamesLost;

	public Team(String teamName){
		this.teamName = teamName;
		this.capturedCards = new CapturedCards();
		this.players = new ArrayList<>();
		this.gamesWon = 0;
		this.gamesLost = 0;
	}
	/**
	 * metodi getters
	 */

	public String getTeamName(){
		return this.teamName;
	}

	public List<Player> getPlayers(){
		return players;
	}

	public CapturedCards getCapturedCards(){
		return capturedCards;
	}

	public int getGamesWon(){
		return this.gamesWon;
	}

	public int getGamesLost(){
		return this.gamesLost;
	}


	/**
	 * metodi adder
	 */
	public void addPlayer(Player player){
		this.players.add(player);
	}

	public void addTrick(Trick trick){
		this.capturedCards.addTrick(trick);
	}

	public void incrementGamesWon(){
		gamesWon++;
	}

	public void incrementGamesLost(){
		gamesLost++;
	}

	/**
	 * metodo che calcola il punteggio delle carte prese
	 *
	 * @param  
	
	 * @return score
	 */
	public int calculateTeamScore(){
		return this.capturedCards.calculateScore();
	}
	
	public void resetCapturedCards() {
		this.capturedCards = new CapturedCards();
	}

}
