package org.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.model.events.*;

public class TressetteGame extends AbstractGame{

	private GameModeStrategy gameMode;
	private final UserProfile userProfile;
	private static final int DEFAULT_WINNING_SCORE = 21;
	private final int teamsCount = 2;

	/**
     * Costruttore che accetta una strategia per la modalità di gioco.
     * @param gameMode La strategia per la configurazione dei giocatori e delle squadre.
     */
    public TressetteGame(GameModeStrategy gameMode,UserProfile userProfile) {
        super();
        this.gameMode = gameMode;
		this.userProfile = userProfile;
        setupGame(userProfile);
    }
	/**
     * Metodo di configurazione che utilizza la strategia per inizializzare giocatori e squadre.
     */
    private void setupGame(UserProfile profile) {
        this.teams = new ArrayList<>();
		this.players.clear();

		int playersPerTeam = gameMode.getPlayersPerTeam();
		int aiCounter = 1;

		for(int t = 0;t<teamsCount;t++){
			Team team = new Team("Team " + (t+1));
			
			for(int p = 0;p<playersPerTeam;p++){
				Player newPlayer;
				// riserva il primo slot al giocatore umano (se presente)
                boolean isHumanSlot = (t == 0 && p == 0) && profile != null;
                if (isHumanSlot) {
                    String nick = profile.getNickname() != null && !profile.getNickname().isBlank()
                            ? profile.getNickname() : "Player";
                    newPlayer = new HumanPlayer(nick);
                } else {
                    newPlayer = new ArtificialPlayer("AI " + (aiCounter++));
                }
                // imposta team usando setTeam per mantenere l'unica sorgente di verità
                newPlayer.setTeam(team);
                // aggiungi alla lista globale di giocatori
                this.players.add(newPlayer);
            }
            this.teams.add(team);
        }

	}

    /**
     * Avvia una nuova partita: mischia il mazzo e distribuisce le carte.
     */
    public void startGame() {
        this.deck.shuffle();
        this.dealCards();
        // Altre inizializzazioni, come la gestione del primo giocatore
    }

    /**
     * Avvia una nuova smazzata: mischia il mazzo e distribuisce le carte.
     */
    public void startRound() {
        this.deck = new Deck(); // Crea un nuovo mazzo per ogni smazzata
        this.deck.shuffle();
        this.dealCards();
        this.currentTrick.clearTrick();
        this.currentPlayerIndex = 0;
		for(Player player: players){
			player.getHand().sort();
		} //ordina le carte delle giocatrici
        
        // La View deve aggiornare lo stato di gioco dopo l'avvio della smazzata
        setChanged();
        notifyObservers(new ModelEventMessage(ModelEvent.ROUND_STARTED,null));
    }
    
    @Override
    public void nextTurn() {
        if (currentTrick.size() < players.size()) {
            Player currentPlayer = players.get(currentPlayerIndex);
            
            // Sposta il turno al prossimo giocatore
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

            setChanged();
            notifyObservers(new ModelEventMessage(ModelEvent.TURN_STARTED, currentPlayerIndex));
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
		Card winningCard = currentTrick.getWinningCard();

		if (winner != null) {
			winner.getTeam().addTrick(currentTrick);
			winnerIndex = players.indexOf(winner);
			this.currentPlayerIndex = winnerIndex;
		}

		// build capturedByPlayer: per ciascun giocatore indice -> carte prese in questa presa
		Map<Integer, List<Card>> capturedByPlayer = new HashMap<>();
		List<Card> captured = currentTrick.getCards();
		// semplificato: assegno l'intera lista al vincitore (puoi invece mappare per giocatore se serve)
		if (winnerIndex >= 0) {
			capturedByPlayer.put(winnerIndex, captured);
		}

		// prepara DTO immutabile prima di svuotare la presa
		TrickResult result = new TrickResult(winnerIndex, winningCard, capturedByPlayer);

		// svuota la presa (i dati già presi nel DTO)
		currentTrick.clearTrick();

		setChanged();
		notifyObservers(new ModelEventMessage(ModelEvent.TRICK_ENDED, result));
	}
    @Override
    public boolean isRoundOver() {
        // La smazzata è finita quando un giocatore non ha più carte
        return players.stream().allMatch(player -> player.getHand().isEmpty());
    }

    @Override
    public boolean isGameOver() {
        for (Team team : teams) {
            // La partita finisce quando una squadra raggiunge o supera i 21 punti
            if (team.calculateTeamScore() >= 21) {
                return true;
            }
        }
        return false;
    }
    
    // Altri metodi utili, come il calcolo del punteggio per la fine della smazzata
    public void calculateRoundScores() {
        for (Team team : teams) {
            int teamScore = team.calculateTeamScore();
            System.out.println("Punteggio " + team.getTeamName() + ": " + teamScore);
        }
    }
}
