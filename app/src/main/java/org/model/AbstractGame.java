package org.model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGame {

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
	}
}


