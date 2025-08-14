package org.model;

import java.util.List;

/**
 * Rappresenta un giocatore controllato dall'intelligenza artificiale.
 * Contiene la logica per decidere quale carta giocare.
 */
public class ArtificialPlayer extends Player {

    public ArtificialPlayer(String name) {
        super(name);
    }

    /**
     * L'intelligenza artificiale gioca una carta.
     * @return La carta che l'IA ha deciso di giocare.
     */
    public Card chooseCardToPlay() {
        // Implementazione di base dell'IA: gioca la prima carta che ha in mano.
        // Questo è il punto in cui puoi implementare strategie di gioco più avanzate.
        List<Card> cardsInHand = getHand().getCards();
        if (!cardsInHand.isEmpty()) {
            return cardsInHand.get(0);
        }
        return null;
    }

    /**
     * Gioca una carta rimuovendola dalla mano, dopo averla scelta con la logica dell'IA.
     * @param card La carta scelta dall'IA.
     */
    @Override
    public void playCard(Card card) {
        if (getHand().containsCard(card)) {
            getHand().removeCard(card);
        }
    }
}
