package org.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

/**
 * classe che modella il mazzo di carte
 */

public class Deck extends CardSet {
	
	/**
	 * il costruttore genera un mazzo da 40 carte e lo mischia
	 *
	 * @author steppasecca
	 */

	public Deck(){
		super(new ArrayList<>());
		initializeDeck();
		shuffle();
	}

	/**
	 * metodo che inizializza il mazzo aggiungendo tutte le carte
	 *
	 * @return void
	 */
	private void initializeDeck(){
		for(Suit suit : Suit.values()){
			for(Rank rank : Rank.values()){
				this.addCard(new Card(suit,rank));
			}
		}
	}

	/**
	 * metodo che mischia le carte
	 *
	 * @return void
	 */
	
	public void shuffle(){
		Collections.shuffle(this.cards);
	}

	public Card drawCard(){
		if (isEmpty()){
			throw new IllegalStateException("il mazzo è vuoto, impossibile pescare");
		}
		return this.cards.remove(this.cards.size()-1);
	}

}
