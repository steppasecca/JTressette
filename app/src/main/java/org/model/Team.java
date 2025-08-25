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
	private List<Player> players; //lista giocatrici nella squadra
	private CapturedCards capturedCards; //lista delle "prese"
	private int roundPoints;  //punti della squadra

	public Team(String teamName){
		this.teamName = teamName;
		this.capturedCards = new CapturedCards();
		this.players = new ArrayList<>();
		this.roundPoints = 0;
	}
	/**
	 *getter per il nome della squadra
	 *@return nome
	 */

	public String getTeamName(){
		return this.teamName;
	}

	/**
	 * @return List of players
	 */

	public List<Player> getPlayers(){
		return players;
	}

	/**
	 * @return Captured Cards
	 */
	public CapturedCards getCapturedCards(){
		return capturedCards;
	}


	/**
	 * @return roundPoints
	 */
	public int getTeamPoints(){
		return this.roundPoints;
	}



	/**
	 * aggiunge un player alla squadra
	 * @param player
	 * @return void
	 */
	public void addPlayer(Player player){
		if(!players.contains(player)){
			this.players.add(player);
		}
	}
	
	/**
	 * aggiunge un trick alle carte prese
	 * @param trick
	 * @return void
	 */
	public void addTrick(Trick trick){
		this.capturedCards.addTrickCards(trick);
	}

	/**
	 * @param roundPoints
	 * @return void
	 */
	public void addRoudPoints(int roundPoints){
		this.roundPoints+=roundPoints;
	}

	/**
	 * metodo che calcola il punteggio delle carte prese
	 * @return score
	 */
	public int calculateTeamPoints(){
		return this.capturedCards.calculatePoints();
	}
	
	public void resetCapturedCards() {
		this.capturedCards = new CapturedCards();
	}

}
