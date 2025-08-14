package org.model;

/**
 * classe che rappresenta una "giocata"
 * quindi un'associazione tra carta giocata e giocatrice/giocatore
 */

public class Play{
	private final Player player;
	private final Card card;

	public Play(Player player,Card card){
		this.player = player;
		this.card = card;
	}

	public Player getPlayer(){return this.player;}
	public Card getCard(){return this.card;}
}

