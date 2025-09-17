package org.view;

import javax.swing.*;
import org.model.UserProfile;
import java.awt.*;

public class StatPanel extends JPanel{

	private JLabel nicknameLabel;
	private JLabel gamesWonLabel;
	private JLabel gamesPlayedLabel;
	private JLabel gamesLostLabel;

	private Runnable onExit;
	private JButton exitButton;

	public StatPanel(){
        // Inizializza le etichette
        nicknameLabel = new JLabel();
        gamesWonLabel = new JLabel();
        gamesPlayedLabel = new JLabel();
        gamesLostLabel = new JLabel();
		exitButton = new JButton();

		//bottone per uscire con listener che collega a callback
		exitButton.addActionListener(e ->{
		   if(onExit!=null)onExit.run();
		});

		initPanel();
	}

	private void initPanel(){
        // Usa un layout per organizzare le etichette
        setLayout(new GridLayout(4, 2, 10, 5));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setBackground(new Color(240, 240, 240));
        // Aggiungi le etichette al pannello
        add(new JLabel("Nickname:"));
        add(nicknameLabel);
        add(new JLabel("Partite Giocate:"));
        add(gamesPlayedLabel);
        add(new JLabel("Partite Vinte:"));
        add(gamesWonLabel);
        add(new JLabel("Partite Perse:"));
        add(gamesLostLabel);
		add(exitButton);
	}

	public void setOnExit(Runnable r){
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

}
