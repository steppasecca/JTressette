package org.model;

import java.util.Objects;

/**
 * rappresenta una singola carta 
 */

public class Card implements Comparable<Card>{
	public enum Suit{
		SPADE,BASTONI,COPPE,DENARI
	}

	private final Suit suit; //seme della carta
	private final Rank rank; //tipo di carta

	public Card(Suit suit, Rank rank){
		this.rank = rank;
		this.suit = suit;
	};

	@Override
	 public String toString(){
		 return rank + "di" + suit; 
	 }

	//metodi getters 
	
	public Suit getSuit(){
		return this.suit;
	}

	public Rank getRank(){
		return this.rank;
	}

	public int getGameValue(){
		return rank.getTressetteValue();
	}
	public int getCaptureOrder(){
		return rank.getCaptureOrder();
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return suit == card.suit && rank == card.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }

	@Override
	public int compareTo(Card other){
		if(this.getSuit() != other.getSuit()){
			throw new IllegalArgumentException("le carte non possono essere confrontate fra semi diversi");
		}
		return Integer.compare(this.getCaptureOrder(),other.getCaptureOrder());
	}

}
