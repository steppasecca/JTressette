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
        setBackground(new Color(15, 60, 40)); // Verde scuro
    }

    public void initGP(List<org.model.Player> players) {
        // definisco i pannelli interni
        handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        handPanel.setBackground(new Color(20, 70, 50));
        
        scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBackground(new Color(20, 70, 50));
        scorePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tablePanel = new TablePanel(players, animationLoop);

        // imposto il TablePanel come target per il repaint centralizzato
        animationLoop.setRepaintTarget(tablePanel);

        // bottone per la pausa
        pauseButton = new JButton("⏸ Pausa");
        pauseButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setBackground(new Color(60, 60, 60));
        pauseButton.setFocusPainted(false);
        pauseButton.setBorderPainted(false);
        pauseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        pauseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pauseButton.setBackground(new Color(80, 80, 80));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pauseButton.setBackground(new Color(60, 60, 60));
            }
        });
        
        pauseButton.addActionListener(e -> {
            if (onPause != null) {
                onPause.run();
            }
        });

        // in alto la scoreBoard
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(15, 60, 40));
        
        JLabel scoreTitle = new JLabel(" 📊 Punteggi e Squadre");
        scoreTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        scoreTitle.setForeground(new Color(212, 175, 55)); // Oro
        top.add(scoreTitle, BorderLayout.NORTH);
        top.add(scorePanel, BorderLayout.CENTER);
        top.add(pauseButton, BorderLayout.EAST);

        // in basso la mano e i pulsanti
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(new Color(15, 60, 40));
        JPanel bottomTop = new JPanel(new BorderLayout());
        bottomTop.setBackground(new Color(15, 60, 40));
        
        JLabel handLabel = new JLabel(" 🃏 La tua mano:");
        handLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        handLabel.setForeground(new Color(212, 175, 55)); // Oro
        bottomTop.add(handLabel, BorderLayout.NORTH);
        handPanel.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2));
        bottomTop.add(new JScrollPane(handPanel), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controls.setBackground(new Color(15, 60, 40));
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
        
        StringBuilder msg = new StringBuilder("🏁 Fine del round!\n\n");
        for (Team t : teams) {
            msg.append(t.getTeamName()).append(": ").append(t.getTeamPoints()).append(" punti\n");
        }
        
        JOptionPane.showMessageDialog(this, msg.toString(), "Round Terminato", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleGameOver() {
        JOptionPane.showMessageDialog(this, "🏆 La partita è finita!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
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
            b.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
            b.setBackground(new Color(30, 80, 60));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Effetto hover
            b.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    b.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    b.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
                }
            });
            
            b.addActionListener(e -> cardClickListener.accept(c));
            handPanel.add(b);
        }
        handPanel.revalidate();
        handPanel.repaint();
    }

    public void updateScores(List<Team> teams) {
        scorePanel.removeAll();
        
        for (Team t : teams) {
            // Box per ogni squadra
            JPanel teamBox = new JPanel();
            teamBox.setLayout(new BoxLayout(teamBox, BoxLayout.Y_AXIS));
            teamBox.setBackground(new Color(40, 100, 70));
            teamBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(212, 175, 55), 2),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            teamBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Nome squadra e punteggio
            JLabel teamLabel = new JLabel(t.getTeamName() + " - Punti: " + t.getTeamPoints());
            teamLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
            teamLabel.setForeground(new Color(255, 215, 0)); // Oro brillante
            teamLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            teamBox.add(teamLabel);
            
            teamBox.add(Box.createVerticalStrut(5));
            
            // Giocatori della squadra
            for (Player p : t.getPlayers()) {
                String icon = (p instanceof HumanPlayer) ? "👤" : "🤖";
                String playerText = "  " + icon + " " + p.getNome();
                
                JLabel playerLabel = new JLabel(playerText);
                playerLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                playerLabel.setForeground(Color.WHITE);
                playerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                teamBox.add(playerLabel);
            }
            
            scorePanel.add(teamBox);
            scorePanel.add(Box.createVerticalStrut(10));
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
