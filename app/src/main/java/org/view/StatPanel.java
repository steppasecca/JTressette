package org.view;

import javax.swing.*;
import org.model.UserProfile;
import java.awt.*;

public class StatPanel extends JPanel {

    private JLabel nicknameLabel;
    private JLabel gamesWonLabel;
    private JLabel gamesPlayedLabel;
    private JLabel gamesLostLabel;

    private Runnable onExit;
    private JButton exitButton;

    public StatPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        // Inizializza le etichette
        nicknameLabel = new JLabel();
        gamesWonLabel = new JLabel();
        gamesPlayedLabel = new JLabel();
        gamesLostLabel = new JLabel();
        exitButton = new JButton("Esci");

        // Bottone per uscire con listener che collega a callback
        exitButton.addActionListener(e -> {
            if (onExit != null) onExit.run();
        });

        initPanel();
    }

    private void initPanel() {
        // Pannello centrale con sfondo semi-trasparente (simile a PauseMenuPanel)
        JPanel box = new JPanel(new GridLayout(9, 1, 10, 10));
        box.setBackground(new Color(0, 0, 0, 180)); // nero semi-trasparente
        box.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Titolo
        JLabel titleLabel = new JLabel("Statistiche", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(Color.WHITE);
        box.add(titleLabel);

        // Aggiungi le etichette al pannello
        JLabel nicknameDescLabel = new JLabel("Nickname:", SwingConstants.CENTER);
        nicknameDescLabel.setForeground(Color.WHITE);
        box.add(nicknameDescLabel);
        
        nicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nicknameLabel.setForeground(Color.WHITE);
        box.add(nicknameLabel);

        JLabel gamesPlayedDescLabel = new JLabel("Partite Giocate:", SwingConstants.CENTER);
        gamesPlayedDescLabel.setForeground(Color.WHITE);
        box.add(gamesPlayedDescLabel);
        
        gamesPlayedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gamesPlayedLabel.setForeground(Color.WHITE);
        box.add(gamesPlayedLabel);

        JLabel gamesWonDescLabel = new JLabel("Partite Vinte:", SwingConstants.CENTER);
        gamesWonDescLabel.setForeground(Color.WHITE);
        box.add(gamesWonDescLabel);
        
        gamesWonLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gamesWonLabel.setForeground(Color.WHITE);
        box.add(gamesWonLabel);

        JLabel gamesLostDescLabel = new JLabel("Partite Perse:", SwingConstants.CENTER);
        gamesLostDescLabel.setForeground(Color.WHITE);
        box.add(gamesLostDescLabel);
        
        gamesLostLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gamesLostLabel.setForeground(Color.WHITE);
        box.add(gamesLostLabel);

        box.add(exitButton);

        add(box, new GridBagConstraints());
    }

    public void setOnExit(Runnable r) {
        this.onExit = r;
    }

    /**
     * Metodo pubblico per aggiornare le etichette 
     * @param profile Il profilo utente con i dati aggiornati.
     */
    public void updateStats(UserProfile profile) {
        if (profile != null) {
            nicknameLabel.setText(profile.getNickname());
            gamesPlayedLabel.setText(String.valueOf(profile.getGamesPlayed()));
            gamesWonLabel.setText(String.valueOf(profile.getGamesWon()));
            int lost = profile.getGamesPlayed() - profile.getGamesWon();
            gamesLostLabel.setText(String.valueOf(lost));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Disegna l'effetto velato sullo sfondo (identico a PauseMenuPanel)
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }
}
