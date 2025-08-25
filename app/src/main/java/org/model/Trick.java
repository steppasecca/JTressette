package org.model;

import java.util.List;
import java.util.ArrayList;

/**
 * classe che rappresenta le carte sul tavolo durante una mano di gioco
 */

public class Trick {

	private Play firstPlay = null;
	private final List<Play> plays  = new ArrayList<>();

	/**
	 * aggiunge una giocata alla lista delle giocate
	 *
	 * @param player
	 * @param card
	 * @return void
	 */
	public void addPlay(Play play){
		plays.add(play);
		if(plays.size() == 1) firstPlay = play;
	}

	/**
	 * ritorna la lista delle giocate
	 * @return plays
	 */
	public List<Play> getPlays(){
		//per quale ragione tornare un nuovo oggetto?
		return new ArrayList<>(plays);
	}

	/**
	 * metodo che ritorna la carta vincente
	 * @return winningCard
	 */
	public Card getWinningCard() {
        if (plays.isEmpty()) return null;
        Card winning = firstPlay.getCard();
        for (Play p : plays) {
            Card c = p.getCard();
            if (c.getSuit() == firstPlay.getCard().getSuit()) {
                if (c.getCaptureOrder() > winning.getCaptureOrder()) {
                    winning = c;
                }
            }
        }
        return winning;
    }

	/**
	 * metodo che ritorna il giocatore vincitore
	 * @return winningPlayer
	 */
    public Player getWinningPlayer() {
        Card winningCard = getWinningCard();
        if (winningCard == null) return null;
        for (Play p : plays) {
            if (p.getCard().equals(winningCard)) return p.getPlayer();
        }
        return null;
    }

	/**
	 * metodo che ritorna la lista delle carte nel trick e lo pulisce
	 * @return cards
	 */
    public List<Card> getAndClearTrick() {
        List<Card> cards = new ArrayList<>();
        for (Play p : plays) cards.add(p.getCard());
        plays.clear();
        firstPlay = null;
        return cards;
    }

	/**
	 * metodo che ritorna le carte nel trick
	 * (penso si possa eliminare)
	 * @return cards
	 */
	public List<Card> getCards(){
		List<Card> cards = new ArrayList<>();
        for (Play p : plays) cards.add(p.getCard());
		return cards;
	}

    public boolean isEmpty() { return plays.isEmpty(); }
    public int size() { return plays.size(); }
}

