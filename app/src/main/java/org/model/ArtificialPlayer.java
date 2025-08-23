package org.model;

import java.util.List;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Card chooseCardToPlay(Trick currentTrick) {
        List<Card> cardsInHand = getHand().getCards();

		if(cardsInHand.isEmpty()){return null;}

        if (currentTrick.isEmpty()) {
			return cardsInHand.stream()
				.max(Comparator.comparingInt(Card :: getCaptureOrder))
				.orElse(cardsInHand.get(0));
		}

		//ottengo il seme della prima carta giocata
		Suit leadingSuit = currentTrick.getPlays().get(0).getCard().getSuit();
        
		//filtro le carte che corrispondono a questo seme
		List<Card> matchingSuitCards = cardsInHand.stream()
			.filter(card -> card.getSuit() == leadingSuit)
			.collect(Collectors.toList());
		if (!matchingSuitCards.isEmpty()) {
			return matchingSuitCards.stream()
				.max(Comparator.comparingInt(Card::getCaptureOrder))
				.orElseGet(() -> cardsInHand.stream()
						.max(Comparator.comparingInt(Card::getCaptureOrder))
						.get());
		} else {
			return cardsInHand.stream()
				.min(Comparator.comparingInt(Card::getCaptureOrder))
				.get();
		}
    }
}
