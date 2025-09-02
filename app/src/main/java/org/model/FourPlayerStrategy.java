package org.model;

import java.util.ArrayList;
import java.util.List;

/**
 * strategia per la modalità di gioco a quattro giocatori
 */

public class FourPlayerStrategy implements GameModeStrategy {

	@Override
	public int getCardsPerPlayer(){return 10;}

	@Override
	public int getPlayersPerTeam(){return 2;}

	@Override
	public void dealInitialCards(List<Player> players , Deck deck){
		for(int i = 0;i<getCardsPerPlayer();i++){
			for (Player p :players){
				if(p.getHand() == null){ p.setHand(new Hand());}
				p.getHand().addCard(deck.drawCard());
			}
		}
	}

	@Override
	public void handlePostTrickDraw(Deck deck,List<Player> players,Player trickWinner){
		return;
	}

}
