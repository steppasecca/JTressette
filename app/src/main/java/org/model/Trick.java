package org.model;

import java.util.List;
import java.util.ArrayList;

/**
 * classe che rappresenta le carte sul tavolo durante una mano di gioco
 */

public class Trick extends CardSet{
	
	private Card firstCardPlayed;

	public Trick(){
		super(new ArrayList<>());
		this.firstCardPlayed = null;
	}
	/**
     * Aggiunge una carta al trick sul tavolo.
     *
     * @param card La carta da aggiungere.
     */
    @Override
    public void addCard(Card card) {
        super.addCard(card);
        if (this.cards.size() == 1) {
            this.firstCardPlayed = card;

		}
        }
	/**
     * Determina quale carta ha vinto la presa secondo le regole del Tressette.
     * da valutare penso sia opportuno ritorni il giocatore che ha preso piuttosto che la carta o se implementare questo come un meccanismo separato
     * @return La carta vincente del trick.
     */
    public Card getWinningCard() {
        if (this.isEmpty()) {
            return null;
        }
        Card winningCard = firstCardPlayed;
        if (firstCardPlayed == null) return null; // O gestisci errore

        for (Card card : this.cards) {
            if (card.getSuit() == firstCardPlayed.getSuit()) {
                if (card.getCaptureOrder() > winningCard.getCaptureOrder()) {
                    winningCard = card;
                }
            }
        }
        return winningCard;
    }
    /**
     * Resetta il trick (svuota le carte sul tavolo).
     * @return Le carte che erano nel trick.
     */
    public List<Card> clearTrick() {
        List<Card> clearedCards = new ArrayList<>(this.cards);
        this.cards.clear();
        this.firstCardPlayed = null;
        return clearedCards;
    }
    }
