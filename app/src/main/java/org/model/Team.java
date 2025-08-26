package org.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * classe che rappresenta una squadra
 *
 * @author steppasecca
 */

public class Team{

	private String teamName;
	private List<Player> players; //lista giocatrici nella squadra
	private CapturedCards capturedCards; //lista delle "prese"
	private int totalPoints; // punti della squadra

	public Team(String teamName){
		this.teamName = teamName;
		this.capturedCards = new CapturedCards();
		this.players = new ArrayList<>();
		this.totalPoints = 0;
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
		return this.totalPoints;
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
	 * metodo che calcola il punteggio delle carte prese
	 * @return score
	 */
	public int calculateCapturedPoints(){
		return this.capturedCards.calculatePoints();
	}

	/**
	 * metodo che aggiorna il punteggio totale alla fine di un round
	 *
	 * @param bonusPoint boolean
	 * @return void
	 */
	public void updateTotalScore(boolean bonusPoint){
		this.totalPoints += calculateCapturedPoints();
		if(bonusPoint){this.totalPoints++;}
		resetCapturedCards();
	}
	
	public void resetCapturedCards() {
		this.capturedCards = new CapturedCards();
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Team team = (Team) o;
		return Objects.equals(teamName, team.teamName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(teamName);
	}
}
