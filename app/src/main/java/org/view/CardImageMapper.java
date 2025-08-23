package org.view;

import org.model.Card;
import org.model.Rank;
import org.model.Suit;

/**
 * Mappa una Card al relativo filename (es. asso_di_spade.jpg).
 */
public final class CardImageMapper {

    private CardImageMapper() {}

    public static String filenameFor(Card card) {
        if (card == null) return null;
        return rankToken(card.getRank()) + "_di_" + suitToken(card.getSuit()) + ".jpg";
    }

    private static String rankToken(Rank r) {
        switch (r) {
            case ASSO: return "asso";
            case DUE: return "due";
            case TRE: return "tre";
            case QUATTRO: return "quattro";
            case CINQUE: return "cinque";
            case SEI: return "sei";
            case SETTE: return "sette";
            case FANTE: return "otto";
            case CAVALLO: return "nove";
            case RE: return "dieci";
            default: return r.name().toLowerCase();
        }
    }

    private static String suitToken(Suit s) {
        switch (s) {
            case SPADE: return "spade";
            case BASTONI: return "bastoni";
            case COPPE: return "coppe";
            case DENARI: return "denari";
            default: return s.name().toLowerCase();
        }
    }
}
