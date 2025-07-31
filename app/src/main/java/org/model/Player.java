package org.model;

/**
 * classe che rappresenta un giocatore nel gioco del tressette
 *
 * @author steppasecca
 */

public abstract class Player{

	private String nome;
	private Hand hand; 
	private Team team;

	public Player(String nome){
		this.nome = nome;
		this.hand = null;
		this.team = null;
	}
	
	/**
	 * il metodo gioca una carta dalla mano del giocatr.
	 *
	 * @param card 
	
	 * @return void
	 */
	public void playCard(Card card){
		hand.playCard(card);
	}

	/**
	 * metodo che ritorna il nome della giocatrice
	 *
	 * @param  
	
	 * @return name String
	 */

	public String getNome(){
		return this.nome;
	}

	public Hand getHand(){
		return hand;
	}

	public Team getTeam(){
		return this.team;
	}

	public void setNome(String nome){
		this.nome = nome;
	}

	public void setHand(Hand hand){
		this.hand = hand;
	}

	public void setTeam(Team team){
		this.team = team;
		if(team != null){
			team.addPlayer(this);
		}
	}


}
