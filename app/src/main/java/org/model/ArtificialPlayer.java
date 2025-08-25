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
		List<Card> hand = getHand().getCards();
		if (hand.isEmpty()) return null; // non dovrebbe capitare, ma è sicuro gestirlo

		// Ordini utili
		Comparator<Card> byOrderAsc  = Comparator.comparingInt(Card::getCaptureOrder); // più bassa prima
		Comparator<Card> byOrderDesc = byOrderAsc.reversed();

		// Helper: tra più carte, prova prima a scegliere una senza punti (gameValue == 0), altrimenti la più bassa
		java.util.function.Supplier<Card> lowestOverall =
			() -> hand.stream().min(byOrderAsc).orElse(hand.get(0));
		java.util.function.Supplier<Card> lowestNoPointsOverall =
			() -> hand.stream()
			.filter(c -> c.getGameValue() == 0)
			.min(byOrderAsc)
			.orElse(null);

		// 1) Sei il primo a giocare nel Trick
		if (currentTrick.isEmpty()) {
			// Apri (se possibile) con la più bassa senza punti; altrimenti con la più bassa in assoluto
			Card np = lowestNoPointsOverall.get();
			return (np != null) ? np : lowestOverall.get();
		}

		// 2) Devi rispondere al seme (se lo hai)
		Card leading = currentTrick.getPlays().get(0).getCard();
		Card winningSoFar = currentTrick.getWinningCard(); // carta attualmente vincente nel trick

		List<Card> sameSuit = hand.stream()
			.filter(c -> c.getSuit() == leading.getSuit())
			.collect(java.util.stream.Collectors.toList());

		if (!sameSuit.isEmpty()) {
			// Tra le carte del seme, cerca la più bassa che BATTE la vincente attuale
			Optional<Card> smallestThatBeats = sameSuit.stream()
				.filter(c -> c.getCaptureOrder() > winningSoFar.getCaptureOrder())
				.min(byOrderAsc);

			if (smallestThatBeats.isPresent()) {
				// Puoi vincere: batti spendendo il minimo indispensabile
				return smallestThatBeats.get();
			} else {
				// Non puoi vincere: risparmia → la più bassa del seme, preferendo carte senza punti
				Card lowestNoPointsSameSuit = sameSuit.stream()
					.filter(c -> c.getGameValue() == 0)
					.min(byOrderAsc)
					.orElse(null);
				return (lowestNoPointsSameSuit != null)
					? lowestNoPointsSameSuit
					: sameSuit.stream().min(byOrderAsc).get();
			}
		}

		// 3) Non hai il seme richiesto: scarta (meglio una carta senza punti, altrimenti la più bassa)
		Card np = lowestNoPointsOverall.get();
		return (np != null) ? np : lowestOverall.get();
	}
}
