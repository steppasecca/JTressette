package org.model;

import java.util.List;

/**
 * Interfaccia per il pattern strategy che definisce la modalità di gioco
 *
 * @author steppasecca
 */

public interface GameModeStrategy {
	int getPlayersPerTeam();

	int getCardsPerPlayer();

	void dealInitialCards(List<Player> players,Deck deck);

	void handlePostTrickDraw(Deck deck,List<Player> players,Player trickWinner); 
	
	}
