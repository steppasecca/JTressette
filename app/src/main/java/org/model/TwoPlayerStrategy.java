package org.model;

import java.util.ArrayList;
import java.util.List;

/**
 * strategia per la modalità di gioco a quattro giocatori
 */

public class TwoPlayerStrategy implements GameModeStrategy {

	@Override
	public int getPlayersPerTeam(){return 1;}

}
