package org.view;

import org.model.UserProfile;
import java.awt.*;
import javax.swing.*;

/**
 * menu di opzioni per modificare i dati del profilo
 */
public class OptionsDialog extends JDialog{

	public OptionsDialog(UserProfile profile){
		setTitle("Opzioni profilo");
		setModal(true);
		setSize(300,200);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel(new GridLayout(3,1,5,5));

		//campo nickname
		JTextField nicknameField = new JTextField(profile.getNickname());
		panel.add(new JLabel("Nickname:"));
		panel.add(nicknameField);

		//pulsante per selezionare Avatar
		JButton avatarButton = new JButton("scegli avatar");
		avatarButton.addActionListener(e-> {
			JFileChooser chooser = new JFileChooser();
			if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
				profile.setAvatarPath(chooser.getSelectedFile().getAbsolutePath());
			}
		});
		panel.add(avatarButton);

		JButton saveButton = new JButton("Salva");
		saveButton.addActionListener(e-> {
			profile.setNickname(nicknameField.getText().trim());
			dispose();
		});
	
        add(panel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
	}


}
