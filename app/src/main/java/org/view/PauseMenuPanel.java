package org.view;

import javax.swing.*;
import java.awt.*;

import  org.util.AudioManager;


public class PauseMenuPanel extends JPanel{

	private Runnable onResume;
	private Runnable onToggleMusic;
	private Runnable onReturnToMenu;

	public PauseMenuPanel(){
		setLayout(new GridBagLayout());
		setOpaque(false);

		//pannello centrale in cui mettere i bottoni
		JPanel box = new JPanel(new GridLayout(3,1,10,10));
		box.setBackground(new Color(0,0,0,180));//dovrebbe essere nero semi-trasparente
		box.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

		//bottoni
		JButton resumeButton = new JButton("resume");
		JButton musicButton = new JButton(AudioManager.getInstance().isPlaying() ? "take music off" : "take music on" );
		JButton mainMenuButton = new JButton("torna al menu principale");

		//resumeButton listener

		resumeButton.addActionListener(e -> {
			if(onResume != null)onResume.run();
		});

		//musicButton listener

		musicButton.addActionListener(e -> {
			if(onToggleMusic != null) onToggleMusic.run();
		});

		//mainMenuButton listener

		mainMenuButton.addActionListener(e -> {
			if(onReturnToMenu != null) onReturnToMenu.run();
		});
		
		box.add(resumeButton);
		box.add(musicButton);
		box.add(mainMenuButton);

		add(box,new GridBagConstraints());
	}

    public void setOnResume(Runnable r) { this.onResume = r; }
    public void setOnToggleMusic(Runnable r) { this.onToggleMusic = r; }
    public void setOnReturnToMenu(Runnable r) { this.onReturnToMenu = r; }

	@Override
	protected void paintComponent(Graphics g){

		super.paintComponent(g);

		//tentativo di disegno di un effetto velato sul gamePanel

		Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
	}
}
