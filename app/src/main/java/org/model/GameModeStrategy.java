package org.model;

import java.util.List;

/**
 * Interfaccia per il pattern strategy che definisce la modalità di gioco
 *
 * @author steppasecca
 */

public interface GameModeStrategy {

	
	/**
     * Configura i giocatori e le squadre per la partita.
     * @return La lista delle squadre create.
     */
    List<Team> setupGame();
}
