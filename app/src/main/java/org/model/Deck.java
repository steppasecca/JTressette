package org.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

/**
 * classe che rappresenta il mazzo di carte
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
	 * @return void
	 */
	private void initializeDeck(){
		for(Card.Suit suit : Card.Suit.values()){
			for(Card.Rank rank : Card.Rank.values()){
				this.addCard(new Card(suit,rank));
			}
		}
	}

	/**
	 * metodo che mischia le carte
	 * @return void
	 */
	
	public void shuffle(){
		Collections.shuffle(this.cards);
	}

	/**
	 * metodo che rappresenta il pescare una carta
	 *
	 * @return card Card la carta pescata
	 */
	public Card drawCard(){
		if (isEmpty()){
			throw new IllegalStateException("il mazzo è vuoto, impossibile pescare");
		}
		return this.cards.remove(this.cards.size()-1);
	}

}
