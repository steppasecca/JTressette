package org.view;

import org.model.UserProfile;

import javax.swing.*;
import java.awt.*;

/**
 * classe che crea una schermata di gioco
 */

public class GamePanel extends JPanel {

	private final UserProfile profile;

	public GamePanel(UserProfile profile){
		super(new BorderLayout());

		this.profile = profile;

		add(new JLabel("partita di tressette"),BorderLayout.CENTER);
	}
}

