package org.view;

import javax.swing.*;
import java.awt.*;


public class MainMenuPanel extends JPanel{

	private String titolo = "JTressette";

	//sotto-pannelli
	private JPanel mainPanel;

	//bottoni
	private final  JComboBox<String> modeSelector;

	//callback
	private Runnable onStart;
	private Runnable onToggleOption;

	public MainMenuPanel(){
		super(new GridBagLayout());

		//definisco i bottoni
		JButton startButton = new JButton("Gicca!");
		JButton optionsProfileButton = new JButton("opzioni profilo");

		//aggiungo i callback per i bottoni
		startButton.addActionListener(e-> {
			if(onStart != null){onStart.run();}
		});

		optionsProfileButton.addActionListener( e-> {
			if(onToggleOption!=null){onToggleOption.run();}
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

        gbc.gridy = 4;
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

	/**
	 * getter per il mainPanel
	 * @return mainPanel
	 */
	public JPanel getMainPanel() {return this.mainPanel;}

}
