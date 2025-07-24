package org.model;

import java.util.Objects;

/**
 * rappresenta una singola carta 
 */

public class Card{
	public enum Suit{
		SPADE,BASTONI,COPPE,DENARI
	}
   public enum Rank {
        ASSO(3), DUE(1), TRE(1), QUATTRO(0), CINQUE(0), SEI(0), SETTE(0), FANTE(1), CAVALLO(1), RE(1);

        private final int tressetteValue;

        Rank(int value) {
            this.tressetteValue = value;
        }

        public int getTressetteValue() {
            return tressetteValue;
        }
    }

	private final Suit suit; //seme della carta
	private final Rank rank; //tipo di carta
	private final int value; //valore della carta

	public Card(Suit suit, Rank rank){
		this.rank = rank;
		this.suit = suit;
		this.value = rank.getTressetteValue();
	};



}
