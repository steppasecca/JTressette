package org.model;

import java.util.ArrayList;
import java.util.List;

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
        
        // La View deve aggiornare lo stato di gioco dopo l'avvio della smazzata
        setChanged();
        notifyObservers("roundStarted");
    }
    
    @Override
    public void nextTurn() {
        if (currentTrick.size() < players.size()) {
            Player currentPlayer = players.get(currentPlayerIndex);
            
            // Logica per far giocare una carta al giocatore corrente
            // Questa parte sarà gestita dal Controller, che a sua volta interagirà
            // con la View per i giocatori umani o con l'AI per i giocatori artificiali.
            
            // Esempio: supponiamo che una carta sia stata giocata
            // Card cardToPlay = ...; 
            // currentPlayer.playCard(cardToPlay);
            // currentTrick.addCard(cardToPlay);

            // Sposta il turno al prossimo giocatore
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

            setChanged();
            notifyObservers("turnEnded");
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
		if(winner!=null){
			winner.getTeam().addTrick(currentTrick);
			this.currentPlayerIndex = players.indexOf(winner);
		}
		currentTrick.clearTrick();
        setChanged();
        notifyObservers("trickEnded");
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
