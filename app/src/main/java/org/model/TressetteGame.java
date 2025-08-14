package org.model;

import java.util.List;

public class TressetteGame extends AbstractGame{

	private GameModeStrategy gameMode;

	/**
     * Costruttore che accetta una strategia per la modalità di gioco.
     * @param gameMode La strategia per la configurazione dei giocatori e delle squadre.
     */
    public TressetteGame(GameModeStrategy gameMode) {
        super();
        this.gameMode = gameMode;
        setupGame();
    }
	/**
     * Metodo di configurazione che utilizza la strategia per inizializzare giocatori e squadre.
     */
    private void setupGame() {
        this.teams = gameMode.setupGame();
        
        // Estrai tutti i giocatori dalle squadre in una singola lista
        // per usarla nei metodi dell'AbstractGame come dealCards()
        this.players.clear();
        for (Team team : teams) {
            this.players.addAll(team.getPlayers());
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
