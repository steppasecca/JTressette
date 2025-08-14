package org.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public abstract class AbstractGame extends Observable{

    protected Deck deck;
    protected List<Player> players; 
    protected List<Team> teams;    
    protected Trick currentTrick;
    protected int currentPlayerIndex;

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
					//qui bisogna ritornare un errore
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
		notifyObservers("carte distribuite");
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
    
    // Getters per accedere allo stato del gioco
    public Deck getDeck() {
        return deck;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Team> getTeams() {
        return teams;
    }
    
    public Trick getCurrentTrick() {
        return currentTrick;
    }
    
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
}


