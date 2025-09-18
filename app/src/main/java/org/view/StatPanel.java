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
		JPanel box = new JPanel(new GridLayout(6, 1, 10, 10));
		box.setBackground(new Color(0, 0, 0, 180)); 
		box.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		// Titolo
		JLabel titleLabel = new JLabel("Statistiche", SwingConstants.CENTER);
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
		titleLabel.setForeground(Color.WHITE);
		box.add(titleLabel);

		// Riga Nickname
		nicknameLabel.setForeground(Color.WHITE);
		nicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		box.add(nicknameLabel);

		// Riga Partite Giocate
		gamesPlayedLabel.setForeground(Color.WHITE);
		gamesPlayedLabel.setHorizontalAlignment(SwingConstants.CENTER);
		box.add(gamesPlayedLabel);

		// Riga Partite Vinte
		gamesWonLabel.setForeground(Color.WHITE);
		gamesWonLabel.setHorizontalAlignment(SwingConstants.CENTER);
		box.add(gamesWonLabel);

		// Riga Partite Perse
		gamesLostLabel.setForeground(Color.WHITE);
		gamesLostLabel.setHorizontalAlignment(SwingConstants.CENTER);
		box.add(gamesLostLabel);

		// Bottone Esci
		box.add(exitButton);

		add(box, new GridBagConstraints());
	}

	public void updateStats(UserProfile profile) {
		if (profile != null) {
			nicknameLabel.setText("Nickname: " + profile.getNickname());
			gamesPlayedLabel.setText("Partite Giocate: " + profile.getGamesPlayed());
			gamesWonLabel.setText("Partite Vinte: " + profile.getGamesWon());
			int lost = profile.getGamesPlayed() - profile.getGamesWon();
			gamesLostLabel.setText("Partite Perse: " + lost);
		}
	}

	public void setOnExit(Runnable r) {
		this.onExit = r;
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
