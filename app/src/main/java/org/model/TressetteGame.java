package org.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
                    String nick = profile.getNickname() != null && !profile.getNickname().isBlank()
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

	@Override
	protected void dealCards(){
		gameMode.dealInitialCards(players, deck);
		setChanged();
		notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.CARDS_DEALT,null));
	}


    /**
     * Avvia una nuova partita: mischia il mazzo e distribuisce le carte.
     */
    public void startGame() {
		startRound();
    }

    /**
     * Avvia una nuova smazzata: mischia il mazzo e distribuisce le carte.
     */
    public void startRound() {
        this.deck = new Deck(); // Crea un nuovo mazzo round
        this.deck.shuffle();
        this.dealCards();
        this.currentTrick = new Trick();
        this.currentPlayerIndex = 0;
		for(Player player: players){
			player.getHand().sort();
		} //ordina le carte delle giocatrici
        
        // La View deve aggiornare lo stato di gioco dopo l'avvio della smazzata
        setChanged();
        notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.ROUND_STARTED,null));
    }
    
	/**
	 * gestisceil passaggio al prossimo turno
	 * @return void
	 */
    @Override
    public void nextTurn() {
        if (currentTrick.size() < players.size()) {
            Player currentPlayer = players.get(currentPlayerIndex);
            
            // Sposta il turno al prossimo giocatore
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

            setChanged();
            notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.TURN_STARTED, currentPlayerIndex));
        } else {
            // La presa è finita, determina il vincitore
            endTrick();
        }
    }

    /**
     * Termina la presa, determina il vincitore e assegna le carte.
     */
	private void endTrick() {
		Player winner = currentTrick.getWinningPlayer();
		int winnerIndex = -1;

		//salvo le giocate prima di pulire il trick
		List<Play> plays = currentTrick.getPlays();
		if (winner != null) {
			winner.getTeam().addTrick(currentTrick);
			winnerIndex = players.indexOf(winner);
			this.currentPlayerIndex = winnerIndex;
		}
		Object[] payload = new Object[]{winner, plays};
		setChanged();
		notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.TRICK_ENDED, payload));

		//se ci sono carte si pesca ancora in base alla mode
		if (winner != null) {
			javax.swing.Timer timer = new javax.swing.Timer(500, e -> {
				gameMode.handlePostTrickDraw(deck, players, winner);
				if(gameMode instanceof TwoPlayerStrategy){
					for(Player player : players){
						if (player instanceof HumanPlayer){
							player.getHand().sort();
						}
					}
				}
			
			});
			timer.setRepeats(false);
			timer.start();
		}

		if(isRoundOver()){
			handleRoundEnd();
		} else {
			this.currentTrick = new Trick();
			notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.TURN_STARTED,currentPlayerIndex));
		}
	}

	/**
	 * gestisce la fine di un round
	 * @return void
	 */
	private void handleRoundEnd(){

		Team teamTooksLast = players.get(currentPlayerIndex).getTeam(); //dovrebbe essere il vincitore perché alla fine del trick 
																 //viene comunque cambiato l'indice al vincitore della mano
		for(Team team : teams){
			if(teamTooksLast.equals(team)){ team.updateTotalScore(true);}
			else{ team.updateTotalScore(false);}	
		}

		//notifico la view che il round sia finito
		setChanged();
		notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.ROUND_ENDED,getTeams()));

		//controllo se sa finita la partita forse potrei eliminare il controllo in endTrick()

		if(isGameOver()){
			setChanged();
			notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.GAME_OVER, null));
		} else {
			startRound();
		}
	}

	/**
	 * controlla se una mossa è valida
	 * @param Play play
	 * @return true se la giocata è valida
	 */
	public boolean isValidPlay(Play play) {
		Player player = play.getPlayer();
		Card card = play.getCard();

		// Deve essere il turno giusto
		if (players.get(currentPlayerIndex) != player) return false;

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
	public boolean playCard(Play play){

		if(!isValidPlay(play)){
			return false;
		}

		Player player = play.getPlayer();
		Card card = play.getCard();

		//il giocatore gioca la carte e la aggiunge al trick
		player.playCard(card);
		currentTrick.addPlay(play);

		//notifico la view
		setChanged();
		notifyObservers(new ModelEventMessage(ModelEventMessage.ModelEvent.CARD_PLAYED,play));
		
		//avanza il turno
		nextTurn();

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
