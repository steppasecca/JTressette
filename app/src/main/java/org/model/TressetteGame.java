package org.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

import javax.swing.SwingUtilities;

import org.util.*;

public class TressetteGame extends AbstractGame{

	private GameModeStrategy gameMode; //classe che rappresenta la strategia
	private final UserProfile userProfile; //penso si possa eliminare questo
	private static final int DEFAULT_WINNING_SCORE = 21; //punteggio massimo del tresetto magari poi lo metto come opzione
	private final int teamsCount = 2; //quantità di squadre in una partita del tressette

	/**
	 * Costruttore che accetta una strategia per la modalità di gioco e un profilo utente.
	 * @param gameMode La strategia per la configurazione dei giocatori e delle squadre.
	 * @param userProfile
	 */
	public TressetteGame(GameModeStrategy gameMode,UserProfile userProfile) {
		super();
		this.gameMode = gameMode;
		this.userProfile = userProfile;
		setupGame(userProfile);
	}
	public TressetteGame(GameModeStrategy gameMode) {
		super();
		this.gameMode = gameMode;
		this.userProfile = null;
		setupGame(this.userProfile);
	}
	/**
	 * Metodo di configurazione che utilizza la strategia per inizializzare giocatori e squadre.
	 * @param userProfile
	 * @return void
	 */
	private void setupGame(UserProfile profile) {
		this.teams = new ArrayList<>();
		this.players = new ArrayList<>();
		this.players.clear();

		int playersPerTeam = gameMode.getPlayersPerTeam();
		int aiCounter = 1;

		for(int t = 0;t<teamsCount;t++){
			Team team = new Team("squadra " + (t+1));

			for(int p = 0;p<playersPerTeam;p++){
				Player newPlayer;
				// riserva il primo slot al giocatore umano (se presente)
				boolean isHumanSlot = (t == 0 && p == 0) && profile != null;
				if (isHumanSlot) {
					String nick = profile!=null && profile.getNickname() != null && !profile.getNickname().isBlank()
						? profile.getNickname() : "HumanPlayer";
					newPlayer = new HumanPlayer(nick);
				} else {
					newPlayer = new ArtificialPlayer("ArtificialPlayer " + (aiCounter++));
				}
				newPlayer.setTeam(team);
				// aggiungi alla lista globale di giocatori
				this.players.add(newPlayer);
			}
			this.teams.add(team);
		}

	}

	/**
	 * Notifica la view con lo stato completo del gioco. Utile all'avvio.
	 */
	public void notifyInitialState() {
		Player human = players.stream()
			.filter(p -> p instanceof HumanPlayer)
			.findFirst()
			.orElse(null);

		List<Card> humanHand = (human != null && human.getHand() != null) ? human.getHand().getCards() : Collections.emptyList();
		List<Play> currentPlays = (currentTrick != null) ? currentTrick.getPlays() : Collections.emptyList();
		List<Player> allPlayers = getPlayers();
		List<Team> allTeams = getTeams();
		Integer currentPlayerIndex = getPlayerIndex(getCurrentPlayer());

		Object[] payload = {humanHand, currentPlays, allPlayers, allTeams, currentPlayerIndex};

		setChanged();
		notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.GAME_STATE_UPDATE, payload));
	}
	@Override
	protected void dealCards(){
		gameMode.dealInitialCards(players, deck);
		Player human = players.stream()
			.filter(p -> p instanceof HumanPlayer)
			.findFirst()
			.orElse(null);

		List<Card> humanHand = (human != null) ? human.getHand().getCards() : Collections.emptyList();

		setChanged();
		notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.CARDS_DEALT, humanHand));
	}


	/**
	 * Avvia una nuova partita: mischia il mazzo e distribuisce le carte.
	 */
	public void startGame() {
		startPlayer = players.get((int)(Math.random() * players.size()));
		startRound();
		notifyInitialState();
	}

	/**
	 * Avvia una nuova smazzata: mischia il mazzo e distribuisce le carte.
	 */
	public void startRound() {
		this.deck = new Deck(); // Crea un nuovo mazzo round
		this.deck.shuffle();
		this.dealCards();
		this.currentTrick = new Trick();
		this.currentPlayer = startPlayer;
		for(Player player: players){
			player.getHand().sort();
		} //ordina le carte delle giocatrici
		// La View deve aggiornare lo stato di gioco dopo l'avvio della smazzata
		setChanged();
		notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.ROUND_STARTED,null));
		nextTurn(true);
	}

	/**
	 * gestisce il passaggio al prossimo turno
	 */
	@Override
	public void nextTurn(boolean isStartPlayer) {
		if (currentTrick.size() < players.size()) {
			// Sposta il turno al prossimo giocatore
			if (!isStartPlayer) {
				currentPlayer = players.get((getPlayerIndex(currentPlayer) + 1) % players.size());
			}
			setChanged();
			notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.TURN_STARTED, getPlayerIndex(currentPlayer)));
			if (currentPlayer instanceof ArtificialPlayer) {
				ArtificialPlayer aiPlayer = (ArtificialPlayer) currentPlayer;
				if (aiPlayer.isThinking()) {
					System.out.println("[nextTurn] AI blocked: isThinking=true for " + aiPlayer);
					return;
				}
				aiPlayer.setIsThinking(true);
				javax.swing.Timer thinkTimer = new javax.swing.Timer(1000, e -> {
					try {
						if (currentPlayer != aiPlayer) {
							System.out.println("[AITimer] Skipped: currentPlayer changed");
							return;
						}
						if (currentTrick.size() >= players.size()) {
							System.out.println("[AITimer] Skipped: trick full, size=" + currentTrick.size());
							return;
						}
						Card cardToPlay = aiPlayer.chooseCardToPlay(currentTrick);
						if (cardToPlay != null && aiPlayer.getHand().containsCard(cardToPlay)) {
							Play aiPlay = new Play(aiPlayer, cardToPlay);
							playCard(aiPlay);
						} else {
							System.out.println("[AITimer] No valid card to play for " + aiPlayer);
						}
					} finally {
						aiPlayer.setIsThinking(false);
					}
				});
				thinkTimer.setRepeats(false);
				thinkTimer.start();
			}
		} else {
			endTrick();
		}
	}

	/**
	 * Termina la presa, determina il vincitore e assegna le carte.
	 */
	private void endTrick() {
		Player winner = currentTrick.getWinningPlayer();

		// salvo le giocate prima di pulire il trick
		List<Play> plays = currentTrick.getPlays();
		if (winner != null) {
			winner.getTeam().addTrick(currentTrick);
			this.currentPlayer = winner;
		}

		if (winner != null) {
			// Timer da 1000 ms (1 secondo), parte una sola volta
			new javax.swing.Timer(2000, e -> {
				// Notifico alla view che il trick è terminato
				Object payload = plays;
				setChanged();
				notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.TRICK_ENDED, payload));

				// Poi gestisco la pesca
				gameMode.handlePostTrickDraw(deck, players, winner);

				if (gameMode instanceof TwoPlayerStrategy) {
					for (Player player : players) {
						if (player instanceof HumanPlayer) {
							player.getHand().sort();
							setChanged();
							notifyObservers(new ModelEventMessage(
										ModelEventMessage.ModelEvent.HAND_UPDATE,
										player.getHand()
										));
						}
					}
				}
				if(isRoundOver()){
					handleRoundEnd();
				} else {
					this.currentTrick = new Trick();
					nextTurn(true);	
				}
			}) {{
				setRepeats(false); // esegui solo una volta
				start();
			}};
		}

	}

	/**
	 * gestisce la fine di un round
	 * @return void
	 */
	private void handleRoundEnd(){

		Team teamTooksLast = currentPlayer.getTeam(); //dovrebbe essere il vincitore perché alla fine del trick 
													  //viene comunque cambiato l'indice al vincitore della mano
		for(Team team : teams){
			if(teamTooksLast.equals(team)){ team.updateTotalScore(true);}
			else{ team.updateTotalScore(false);}	
		}

		startPlayer = players.get((players.indexOf(startPlayer) + 1) % players.size());

		//notifico la view che il round sia finito
		setChanged();
		notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.ROUND_ENDED,getTeams()));

		//controllo se sa finita la partita forse potrei eliminare il controllo in endTrick()

		if(isGameOver()){
			handleGameOver();
		} else {
			startRound();
		}
	}

	public void handleGameOver(){
		this.winningTeam = teams.stream()
			.max(Comparator.comparingInt(Team::getTeamPoints))
			.orElseThrow();
		if(userProfile != null){
			Optional<Player> humanPlayer = winningTeam.getPlayers().stream().
				filter(p -> p instanceof HumanPlayer)
				.findFirst();
			if(humanPlayer.isPresent()){
				userProfile.addGame(true);	
			} else {
				userProfile.addGame(false);
			}
			setChanged();
			notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.PROFILE_CHANGED,null));
		}

		setChanged();
		notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.GAME_OVER, null));
	}

	/**
	 * controlla se una mossa è valida
	 * @param Play play
	 * @return true se la giocata è valida
	 */
	public boolean isValidPlay(Play play) {
		Player player = play.getPlayer();
		Card card = play.getCard();

		//controlla se il trick è pieno
		if(currentTrick.getCards().size()>=players.size()){
			return false;
		}
		// Deve essere il turno giusto
		if(!currentPlayer.equals(player)) return false;

		// La carta deve stare nella mano
		if (!player.getHand().containsCard(card)) return false;


		// Se non è il primo della mano, deve rispondere al seme
		if (!currentTrick.isEmpty()) {
			Card leadingCard = currentTrick.getPlays().get(0).getCard();
			boolean hasSuit = player.getHand().getCards().stream()
				.anyMatch(c -> c.getSuit() == leadingCard.getSuit());
			if (hasSuit && card.getSuit() != leadingCard.getSuit()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * esegue una giocata se è valida
	 *
	 * @param play (carta e giocatore che la gioca)
	 * @return true se la carta è stata giocata, false altrimenti
	 */
	/**
	 * esegue una giocata se è valida
	 */
	public synchronized boolean playCard(Play play) {
		if (!isValidPlay(play)) {
			System.out.println("[playCard] Giocata non valida: player=" + play.getPlayer() + ", card=" + play.getCard());
			return false;
		}
		Player player = play.getPlayer();
		Card card = play.getCard();
		player.playCard(card);
		currentTrick.addPlay(play);
		System.out.println("il giocatore" + player.toString() + "ha giocato la carta: " + card.toString());
		Object[] payload = {play, getPlayers()};
		setChanged();
		notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.CARD_PLAYED, payload));
		if (player instanceof HumanPlayer) {
			setChanged();
			notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.HAND_UPDATE, player.getHand()));
		}
		nextTurn(false);
		return true;
	}

	/**
	 * @return true if the round is over
	 */
	@Override
	public boolean isRoundOver() {
		// La smazzata è finita quando un giocatore non ha più carte
		return players.stream().allMatch(player -> player.getHand().isEmpty());
	}

	/**
	 * @return true if the game is over
	 */
	@Override
	public boolean isGameOver() {
		for (Team team : teams) {
			// La partita finisce quando una squadra raggiunge o supera i 21 punti
			if (team.getTeamPoints() >= 21) {
				return true;
			}
		}
		return false;
	}

}
