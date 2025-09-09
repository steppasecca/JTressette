package org.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.util.*;


public abstract class AbstractGame extends Observable{

    protected Deck deck; //mazzo delle carte
    protected List<Player> players; //lista di giocatori/giocatrici
    protected List<Team> teams; //lista delle squadre 
    protected Trick currentTrick; //"giocata" attuale ovvero le carte sul tavolo
    protected Player currentPlayer; //giocator che deve giocare
	protected Player startPlayer; //giocatore che inizia il round

    public AbstractGame() {
        this.deck = null;
        this.players = null;
        this.teams = null;
        this.currentTrick = null;
    }

	/**
	 * metodo astratto per distribuire le carte 
	 *
	 * @return void
	 */

	protected abstract void dealCards(); 
	
    /**
     * Metodo astratto per gestire la logica di un singolo turno.
     */
    public abstract void nextTurn();

    /**
     * Metodo astratto per verificare se una smazzata è terminata.
     * @return true se la smazzata è finita, altrimenti false.
     */
    public abstract boolean isRoundOver();

    /**
     * Metodo astratto per verificare se la partita è terminata.
     * @return true se la partita è finita, altrimenti false.
     */
    public abstract boolean isGameOver();
    
	/**
    * Getters per accedere al mazzo
	*/
    public Deck getDeck() {
        return deck;
    }

	/**
	 * @return players getter per la lista delle giocatrici	
	 */
    public List<Player> getPlayers() {
        return players;
    }

	/**
	 * @return List<Team> teams getter per la lista delle squadre
	 */
    public List<Team> getTeams() {
        return teams;
    }
    
	/**
	 * @return Trick currentTrick il trick corrente 
	 */
    public Trick getCurrentTrick() {
        return currentTrick;
    }
    
	/**
	 * @return indexOf(player) indece del giocatore corrente
	 */
    public int getPlayerIndex(Player player) {
        return players.indexOf(player);
    }
	public Player getCurrentPlayer(){ return currentPlayer;}

	public Player getStartPlayer(){return startPlayer;}
}
