package org.model;

import java.util.Objects;

/**
 * rappresenta una singola carta 
 */

public class Card implements Comparable<Card>{

	private final Suit suit; //seme della carta
	private final Rank rank; //tipo di carta indice di presa e valore nel calcolo del punteggio

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

	public double getGameValue(){
		return rank.getTressetteValue()/3;
	}
	public int getCaptureOrder(){
		return rank.getCaptureOrder();
	}

	/**
	 * ovveride del metodo equals per confrontare due carte
	 * @param Object o
	 * @return true se sono uguali
	 */
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

	/**
	 * metodo per confrontare due carte rispetto al loro ordine di cattura
	 *
	 * @param other Card
	 * @return  un valore positivo se this maggiore other
	 */

	@Override
	public int compareTo(Card other){
		if(this.getSuit() != other.getSuit()){
			throw new IllegalArgumentException("le carte non possono essere confrontate fra semi diversi");
		}
		return Integer.compare(this.getCaptureOrder(),other.getCaptureOrder());
	}
	
	public enum Suit{
		SPADE,BASTONI,COPPE,DENARI
	}

   public enum Rank {
        ASSO(3,8), DUE(1,9), TRE(1,10), 
		QUATTRO(0,1), CINQUE(0,2), SEI(0,3), 
		SETTE(0,4), FANTE(1,5), 
		CAVALLO(1,6), RE(1,7);

        private final int tressetteValue;
		private final int captureOrder;

        Rank(int tressetteValue,int captureOrder) {
            this.tressetteValue = tressetteValue;
			this.captureOrder = captureOrder;
        }

        public int getTressetteValue() {
            return tressetteValue;
        }

		public int getCaptureOrder(){
			return captureOrder;
		}
    }
}
