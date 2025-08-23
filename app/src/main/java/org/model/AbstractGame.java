package org.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.events.*;


public abstract class AbstractGame extends Observable{

    protected Deck deck; //mazzo delle carte
    protected List<Player> players; //lista di giocatori/giocatrici
    protected List<Team> teams; //lista delle squadre 
    protected Trick currentTrick; //"giocata" attuale ovvero le carte sul tavolo
    protected int currentPlayerIndex; //indice del giocator che deve giocare

    public AbstractGame() {
        this.deck = new Deck();
        this.players = new ArrayList<>();
        this.teams = new ArrayList<>(); 
        this.currentTrick = new Trick();
        this.currentPlayerIndex = 0;
    }

	/**
	 * metodo per distribuire le carte ai giocatori
	 *
	 * @return void
	 */

	protected void dealCards() { 
		int cardsPerPlayer = 10;
		for(int i = 0;i<cardsPerPlayer;i++){
			for(Player player : players){
				if(deck.isEmpty()){
					throw new IllegalStateException("errore ho cercato di distribuire carte da un mazzo vuoto");
				}
				else{
					if(player.getHand() == null){
						player.setHand(new Hand());
					}
					player.getHand().addCard(deck.drawCard());
				}
			}
		}
		setChanged();
		notifyObservers(new ModelEventMessage(ModelEvent.CARDS_DEALT,null));
	}
	
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
	 * @return currentPlayerIndex indece del giocatore corrente
	 */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
}
