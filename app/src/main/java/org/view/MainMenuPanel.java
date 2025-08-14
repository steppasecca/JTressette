package org.view;

import javax.swing.*;

import java.awt.*;

import org.model.UserProfile;

import java.util.Observer;
import java.util.Observable;

/**
 * la classe menu rappresenta la prima interfaccia del gioco
 */

public class MainMenuPanel extends JPanel implements Observer{

	private final JLabel nicknameLabel = new JLabel();
	private final JLabel gamesLabel = new JLabel();
	private final JLabel winsLabel = new JLabel();

	private JButton startButton = new JButton("nuova partita");
	private JButton optionsProfileButton = new JButton("opzioni profilo");
	private final  JComboBox<String> modeSelector;

	private final UserProfile profile;

	public MainMenuPanel(UserProfile profile){
		super(new GridBagLayout());
		this.profile = profile;
		if(profile != null){
			profile.addObserver(this);
			refreshFromProfile();
		}

		//layout principale
		JPanel inner = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Titolo
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel title = new JLabel("JTressette");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 35f));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        inner.add(title, gbc);

        // Statistiche
        gbc.gridy = 1;
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        statsPanel.add(nicknameLabel);
        statsPanel.add(gamesLabel);
        statsPanel.add(winsLabel);
        inner.add(statsPanel, gbc);

        // Pulsante Nuova Partita
        gbc.gridy = 2;
        inner.add(startButton, gbc);

        // Pulsante Opzioni
        gbc.gridy = 3;
        inner.add(optionsProfileButton, gbc);

        // Selettore modalità di gioco
        gbc.gridy = 4;
        modeSelector = new JComboBox<>(new String[] { "2 Giocatori", "4 Giocatori" });
        inner.add(modeSelector, gbc);

        add(inner);}

    public JButton getStartButton() { return startButton; }
    public JButton getOptionsButton() { return optionsProfileButton; }
    public String getSelectedMode() { return (String) modeSelector.getSelectedItem(); }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof UserProfile) {
            refreshFromProfile();
        }
    }

    private void refreshFromProfile() {
        nicknameLabel.setText("Nickname: " + profile.getNickname());
        gamesLabel.setText("Partite giocate: " + profile.getGamesPlayed());
        winsLabel.setText("Partite vinte: " + profile.getGamesWon());
    }
}
