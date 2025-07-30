package org.model;
/**
 * questa classe rappresenta le carte in che un giocatore/giocatrice ha in mano
 */

import java.util.Collections;
import java.util.ArrayList;

public class Hand extends CardSet{

	public Hand(){
		super(new ArrayList<>());
	}

	/**
	 * il metodo implementa il meccanismo di giocare una carta
	 *
	 * @param card carta da giocare
	 * @return carta giocata
	 */

	public Card playCard(Card card) {
		if (!this.cards.contains(card)){
			throw new IllegalArgumentException("la carta" + card +  "non è presente nel mazzo" ); }
		this.removeCard(card);
		return card;
	}

	/**
	 * metodo che ordina le carte in una mano per Seme
	 * "controllare che vengano ordinate per seme""
	 *
	 * @return void
	 */
	public void sort(){
		Collections.sort(this.cards);
	}



	
}
