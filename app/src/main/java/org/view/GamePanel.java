package org.view;

import javax.swing.*;
import java.awt.*;
import java.util.Observer;
import java.util.Observable;
import java.util.List;
import java.util.ArrayList;
import org.model.*;
import org.util.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * classe che mostra il gioco del tressette 
 */
public class GamePanel extends JPanel implements Observer {

    private TablePanel tablePanel;
    private JPanel handPanel;
    private JPanel scorePanel;

    private Runnable onPause;
    private JButton pauseButton;

    private Consumer<Card> cardClickListener = c -> {};

    // unica instanza di AnimationLoop
    private final AnimationLoop animationLoop;

    public GamePanel() {
        super(new BorderLayout(8, 8));
        this.animationLoop = new AnimationLoop();
    }

    public void initGP(List<org.model.Player> players) {
        // definisco i pannelli interni
        handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        scorePanel = new JPanel(new GridLayout(0, 1));
        tablePanel = new TablePanel(players, animationLoop);

        // IMPORTANTE: Imposta il TablePanel come target per il repaint centralizzato
        animationLoop.setRepaintTarget(tablePanel);

        // definisco il bottone per la pausa
        pauseButton = new JButton("pausa");
        
        pauseButton.addActionListener(e -> {
            if (onPause != null) {
                onPause.run();
            }
        });

        // in alto la scoreBoard
        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("punteggi: "), BorderLayout.NORTH);
        top.add(scorePanel, BorderLayout.CENTER);
        top.add(pauseButton, BorderLayout.EAST);

        // in basso la mano e i pulsanti
        JPanel bottom = new JPanel(new BorderLayout());
        JPanel bottomTop = new JPanel(new BorderLayout());
        bottomTop.add(new Label("Mano: "), BorderLayout.NORTH);
        handPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        bottomTop.add(new JScrollPane(handPanel), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(bottomTop, BorderLayout.CENTER);
        bottom.add(controls, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(bottom, BorderLayout.SOUTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    public void setOnPause(Runnable r) {
        this.onPause = r;
    }

    public void update(Observable o, Object arg) {
        SwingUtilities.invokeLater(() -> {
            if (arg instanceof ModelEventMessage msg) {
                switch (msg.getEvent()) {
                    case CARDS_DEALT -> handleCardsDealt(msg.getPayload());
                    case CARD_PLAYED -> handleCardPlayed(msg.getPayload());
                    case TURN_STARTED -> handleTurnStarted(msg.getPayload());
                    case TRICK_ENDED -> handleTrickEnded((List<Play>) msg.getPayload());
                    case ROUND_STARTED -> handleRoundStarted();
                    case ROUND_ENDED -> handleRoundEnded((List<Team>) msg.getPayload());
                    case GAME_OVER -> handleGameOver();
                    case HAND_UPDATE -> handleHandUpdate((Hand) msg.getPayload());
                    case GAME_STATE_UPDATE -> handleGameStateUpdate(msg.getPayload());
                }
            }
        });
    }

    private void handleCardsDealt(Object payload) {
        if (payload instanceof List) {
            List<Card> hand = (List<Card>) payload;
            updateHand(hand);
        }
    }

    private void handleGameStateUpdate(Object payload) {
        if (payload instanceof Object[] data) {
            updateHand((List<Card>) data[0]);
            updateTable((List<Play>) data[1], (List<Player>) data[2]);
            updateScores((List<Team>) data[3]);
            setCurrentPlayer((Integer) data[4]);
        }
    }

    private void handleCardPlayed(Object payload) {
        if (payload instanceof Object[] data) {
            Play play = (Play) data[0];
            List<Player> players = (List<Player>) data[1];
            int idx = players.indexOf(play.getPlayer());
            
            System.out.println("[HANDLE_CARD_PLAYED] player=" + play.getPlayer() + " idx=" + idx + " card=" + play.getCard());
            
            if (idx >= 0) {
                playCard(idx, play.getCard());
            }
        }
    }

    private void handleTurnStarted(Object payload) {
        if (payload instanceof Integer idx) {
            setCurrentPlayer(idx);
        } else {
            System.err.println("handleTurnStarted: Payload non valido.");
        }
    }

    private void handleTrickEnded(List<Play> plays) {
        List<Player> players = new ArrayList<>();
        for (Play play : plays) {
            players.add(play.getPlayer());
        }
        tablePanel.clearTablePanel();
    }

    private void handleRoundStarted() {
        // La pulizia del tavolo e della mano avverrà con i successivi eventi
    }

    private void handleRoundEnded(List<Team> teams) {
        updateScores(teams);
        JOptionPane.showMessageDialog(this, "Fine della round! Si procede al prossimo.");
    }

    private void handleGameOver() {
        JOptionPane.showMessageDialog(this, "La partita è finita!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleHandUpdate(Hand hand) {
        updateHand(hand.getCards());
    }

    public void updateHand(List<org.model.Card> cards) {
        handPanel.removeAll();
        final int thumbW = 80;
        final int thumbH = 120;
        for (Card c : cards) {
            BufferedImage img = ImageCache.getImageForCard(c);
            JButton b;
            if (img != null) {
                Image scaled = img.getScaledInstance(thumbW, thumbH, Image.SCALE_SMOOTH);
                b = new JButton(new ImageIcon(scaled));
            } else {
                b = new JButton(c.toString());
            }
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createEmptyBorder());
            b.addActionListener(e -> cardClickListener.accept(c));
            handPanel.add(b);
        }
        handPanel.revalidate();
        handPanel.repaint();
    }

    public void updateScores(List<Team> teams) {
        scorePanel.removeAll();
        for (Team t : teams) {
            scorePanel.add(new JLabel(t.getTeamName() + ": " + t.getTeamPoints()));
        }
        scorePanel.revalidate();
        scorePanel.repaint();
    }

    public void updateTable(List<Play> plays, List<Player> players) {
        if (tablePanel == null) return;

        if (plays.isEmpty()) {
            tablePanel.clearTablePanel();
            return;
        }

        for (Play p : plays) {
            Player pl = p.getPlayer();
            int idx = players.indexOf(pl);
            if (idx >= 0) {
                tablePanel.playAnimatedCard(idx, p.getCard(), null);
            }
        }
    }

    public void playCard(int playerIdx, org.model.Card card) {
        if (tablePanel != null) {
            tablePanel.playAnimatedCard(playerIdx, card, () -> {
                System.out.println("[HANDLE_CARD_PLAYED CALLBACK] idx=" + playerIdx + " card=" + card.toString());
            });
        }
    }

    public void setCurrentPlayer(int playerIndex) {
        if (tablePanel != null) {
            tablePanel.setCurrentPlayer(playerIndex);
        }
    }

    public void setCardClickListener(Consumer<Card> listener) {
        this.cardClickListener = listener;
    }

    public AnimationLoop getAnimationLoop() {
        return this.animationLoop;
    }
}
