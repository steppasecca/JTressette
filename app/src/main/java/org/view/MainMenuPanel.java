package org.view;

import javax.swing.*;
import java.awt.*;


public class MainMenuPanel extends JPanel{

	//sotto-pannelli
	private JPanel mainPanel;

	//bottoni
	private final  JComboBox<String> modeSelector;

	//callback
	private Runnable onStart;
	private Runnable onToggleOption;
	private Runnable onToggleStat;

	public MainMenuPanel(){
		super(new GridBagLayout());

		//definisco i bottoni
		JButton startButton = new JButton("Gioca!");
		JButton optionsProfileButton = new JButton("opzioni profilo");
		JButton statButton = new JButton("statistiche di gioco");

		//aggiungo i callback per i bottoni
		startButton.addActionListener(e-> {
			if(onStart != null){onStart.run();}
		});

		optionsProfileButton.addActionListener( e-> {
			if(onToggleOption!=null){onToggleOption.run();}
		});
		statButton.addActionListener(e -> {
			if(onToggleStat!=null){onToggleStat.run();}
		});
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

        // Pulsante Nuova Partita
        gbc.gridy = 2;
        inner.add(startButton, gbc);

        // Pulsante Opzioni
        gbc.gridy = 3;
        inner.add(optionsProfileButton, gbc);

		//pulsante stat
		gbc.gridy = 4;
		inner.add(statButton,gbc);

		//selettore di modalità
        gbc.gridy = 5;
        modeSelector = new JComboBox<>(new String[] { "2 Giocatori", "4 Giocatori" });
        inner.add(modeSelector, gbc);

        add(inner);
	}

	//setter per i callback
	public void setOnStart(Runnable r){
		this.onStart = r;
	}

	public void setOnToggleOption(Runnable r){
		this.onToggleOption = r;
	}
	public void setOnToggleStat(Runnable r){
		this.onToggleStat = r;
	}

	/**
	 * getter per il mainPanel
	 * @return mainPanel
	 */
	public JPanel getMainPanel() {return this.mainPanel;}

}
