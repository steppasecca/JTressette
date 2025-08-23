package org.model;

/**
 * classe che rappresenta un giocatore nel gioco del tressette
 *
 * @author steppasecca
 */

public abstract class Player{

	private String nome;
	private Hand hand; //mano ,insieme delle carte giocabili da un giocatore 
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
	 * @return name String
	 */
	public String getNome(){
		return this.nome;
	}

	/**
	 * getter per la mano del giocatrice
	 * @return hand
	 */
	public Hand getHand(){
		return hand;
	}

	/**
	 * getter per la squadra del giocatore
	 * @return team
	 */
	public Team getTeam(){
		return this.team;
	}

	/**
	 * setter per il nome del giocatore
	 * @param String nome
	 * @return void
	 */
	public void setNome(String nome){
		this.nome = nome;
	}

	/**
	 * setter per la mano
	 * @param Hand hand
	 * @return void
	 */
	public void setHand(Hand hand){
		this.hand = hand;
	}

	/**
	 * setta la squadra e aggiunge il giocatore alla squadra stessa
	 * @param Team team
	 * @return void
	 */
	public void setTeam(Team team){
		this.team = team;
		if(team != null){
			team.addPlayer(this);
		}
	}
}
