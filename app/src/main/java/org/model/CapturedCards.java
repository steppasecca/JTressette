package org.model;

import java.util.ArrayList;

/**
 * classe che rappresenta le carte "prese" da un player o da una squadra
 */

public class CapturedCards extends CardSet{

	public CapturedCards(){
		super(new ArrayList<>());
	}

	/**
     * Calcola il punteggio totale delle carte in questa pila secondo le regole del Tressette.
     *
     * @return terzi
     */
    public int calculatePoints() {
        int terzi = 0;
        for (Card card : this.cards) {
            terzi += card.getGameValue(); 
        }
        return terzi;
    }

	/**
	 * aggiunge una giocata dal tavolo
	 * @param Trick trick
	 * @return void
	 */
	public void addTrickCards(Trick trick){
		this.cards.addAll(trick.getAndClearTrick());
	}
	
}
