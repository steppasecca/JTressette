package org.model;

import java.util.ArrayList;
import java.util.List;

/**
 * strategia per la modalità di gioco a quattro giocatori
 */

public class TwoPlayerStrategy implements GameModeStrategy {

	@Override
	public List<Team> setupGame(){
		List<Team> teams = new ArrayList<>();

		Team team1 = new Team("squadra1");
		team1.addPlayer(new HumanPlayer("giocatore 1"));
		
		Team team2 = new Team("squadra2");
		team2.addPlayer(new ArtificialPlayer("giocatore 2"));

		teams.add(team1);
		teams.add(team2);

		return teams;
	}

}
