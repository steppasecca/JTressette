
package org.model;

/**
 * Rappresenta un giocatore controllato da un essere umano.
 */
public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
    }

    /**
     * Gioca una carta rimuovendola dalla mano.
     * @param card La carta scelta dall'utente.
     */
    @Override
    public void playCard(Card card) {
        if (getHand().containsCard(card)) {
            getHand().removeCard(card);
        }
    }
}
