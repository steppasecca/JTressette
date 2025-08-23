package org.model.events;


import org.model.Card;
import java.util.List;
import java.util.Map;

public class TrickResult {
    private final int winnerIndex;
    private final Card winningCard;
    private final Map<Integer, List<Card>> capturedByPlayer;

    public TrickResult(int winnerIndex, Card winningCard, Map<Integer, List<Card>> capturedByPlayer) {
        this.winnerIndex = winnerIndex;
        this.winningCard = winningCard;
        this.capturedByPlayer = capturedByPlayer;
    }
    public int getWinnerIndex(){ return winnerIndex; }
    public Card getWinningCard(){ return winningCard; }
    public Map<Integer,List<Card>> getCapturedByPlayer(){ return capturedByPlayer; }
}
