package org.model;

import java.util.ArrayList;
import java.util.List;

/**
 * classe che modella un insieme di carte 
 */

public abstract class CardSet { 

	protected List<Card> cards;


	public CardSet(List<Card> cards){
		this.cards = new ArrayList<>(cards);
	}

	/**
	 * metodo che ritorna la lista delle carte
	 *
	 * @param  
	
	 * @return List<Card> cards
	 */
	public List<Card> getCards(){
		return cards;
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

	public boolean containsCard(Card card){
		return cards.contains(card);
	}
}

