package org.model;

import java.util.ArrayList;
import java.util.List;

/**
 * strategia per la modalità di gioco a quattro giocatori
 */

public class TwoPlayerStrategy implements GameModeStrategy {

	@Override
	public int getCardsPerPlayer(){return 10;}

	@Override
	public int getPlayersPerTeam(){return 1;}

	@Override
	public void dealInitialCards(List<Player> players,Deck deck){
		for(int i = 0; i<getCardsPerPlayer();i++){
			for(Player p : players){
				if(p.getHand() == null){p.setHand(new Hand());}
				p.getHand().addCard(deck.drawCard());
			}
		}
	}

	@Override
	public void handlePostTrickDraw(Deck deck, List<Player> players, Player trickWinner){
		int start = players.indexOf(trickWinner);
		// Fanno il giro a partire dal vincitore: ciascuno pesca una carta se disponibile
		for (int i = 0; i < players.size() && !deck.isEmpty(); i++) {
			Player p = players.get((start + i) % players.size());
			p.getHand().addCard(deck.drawCard());
		}
	}

}
