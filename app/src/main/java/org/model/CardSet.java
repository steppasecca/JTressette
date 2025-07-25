package org.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * classe che modella un insieme di carte 
 */

public abstract class CardSet { 

	protected List<Card> cards;


	public CardSet(List<Card> cards){
		this.cards = new ArrayList<>(cards);
	}

	/**
	 *aggiunge una carta all'insime di carte 
	 *
	 * @param card la carta da aggiungere.
	 */
	public void addCard(Card card){
		this.cards.add(card);
	}

	/**
	 *rimuove una arta specifica dall'insieme 
	 *@param  card la carta da rimuovere
	 * @return true se la carta è stata rimossa con successo
	 */

	public boolean removeCard(Card card){
		return this.cards.remove(card);
	}

	/**
     * Rimuove e restituisce la carta in cima/fine dell'insieme (logica specifica da implementare nelle sottoclassi).
     *
     *
     * @return La carta rimossa.
     * @throws IllegalStateException se l'insieme è vuoto.
     */

	public abstract Card drawCard();
	
	/**
	 * 
	 * restituisce il numero di carte dell'insieme
	 * @return int numero di carte
	 */

	public int getSizeOfCardSet(){
		return this.cards.size();
	}

    /**
     * Controlla se l'insieme di carte è vuoto.
     * @return true se l'insieme è vuoto, false altrimenti.
     */
    public boolean isEmpty() {
        return this.cards.isEmpty();
    }

	/**
	 * 
	 *
	
	 * @return description of return value
	 */

}

