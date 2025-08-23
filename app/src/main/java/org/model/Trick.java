package org.model;

import java.util.List;
import java.util.ArrayList;

/**
 * classe che rappresenta le carte sul tavolo durante una mano di gioco
 */

public class Trick {

	private Play firstPlay = null;
	private final List<Play> plays  = new ArrayList<>();

	public void addPlay(Player player ,Card card){
		Play play = new Play(player,card);
		plays.add(play);
		if(plays.size() == 1) firstPlay = play;
	}

	public List<Play> getPlays(){
		//per quale ragione tornare un nuovo oggetto?
		return new ArrayList<>(plays);
	}
	
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
    public Player getWinningPlayer() {
        Card winningCard = getWinningCard();
        if (winningCard == null) return null;
        for (Play p : plays) {
            if (p.getCard().equals(winningCard)) return p.getPlayer();
        }
        return null;
    }

    public List<Card> getAndClearTrick() {
        List<Card> cards = new ArrayList<>();
        for (Play p : plays) cards.add(p.getCard());
        plays.clear();
        firstPlay = null;
        return cards;
    }

	public List<Card> getCards(){
		List<Card> cards = new ArrayList<>();
        for (Play p : plays) cards.add(p.getCard());
		return cards;
	}


	
	
    public boolean isEmpty() { return plays.isEmpty(); }
    public int size() { return plays.size(); }
}

