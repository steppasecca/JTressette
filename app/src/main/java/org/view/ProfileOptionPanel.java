package org.view;

import org.model.UserProfile;
import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.io.InputStream;


/**
 * Pannello opzioni per la modifica dei dati del proprio Profilo 
 */

public class ProfileOptionPanel extends JPanel{
	private final UserProfile profile;
	private JTextField nicknameField;
	private JPanel avatarGallery;
	private JButton saveButton;
	private JButton cancelButton;
	private String selectedAvatarPath;

	private Runnable onSave;
	private Runnable onCancel;

	// Lista degli avatar disponibili (nomi dei file nella cartella resources/images/avatars/)
    private static final String[] AVATAR_FILES = {
        "avatar1.png", "avatar2.png", "avatar3.png", "avatar4.png",
        "avatar5.png", "avatar6.png", "avatar7.png", "avatar8.png"
    };
    
    private static final int AVATAR_SIZE = 64;
    private static final int AVATAR_PADDING = 8;

	public ProfileOptionPanel(UserProfile profile){
		this.profile = profile;
		this.selectedAvatarPath = profile.getAvatarPath();
		setLayout(new GridBagLayout());
		setOpaque(false);
		initComponents();
	}

	/**
	 * metodo privato di utilità per inizializzare i componenti del pannell
	 */
	private void initComponents() {

		//pannello principale
		JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(240, 240, 240));
		contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

		//titolo
		JLabel titleLabel = new JLabel("opzioni profilo");
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(titleLabel, gbc);

		//reset per gbc per i prox componenti
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;

		//campo nickname
		gbc.gridx = 0; gbc.gridy = 1;
		contentPanel.add(new JLabel("Nickname"),gbc);

		nicknameField = new JTextField(profile.getNickname(),20);
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		contentPanel.add(nicknameField,gbc);

		//campo avatar
		gbc.gridx = 0; gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		JLabel avatarLabel = new JLabel("scegli Avatar");
        avatarLabel.setFont(avatarLabel.getFont().deriveFont(Font.BOLD));
        contentPanel.add(avatarLabel, gbc);

		//galleria avatar
		createAvatarGallery();
		gbc.fill = GridBagConstraints.BOTH;
		contentPanel.add(new JScrollPane(avatarGallery),gbc);

		//statistiche
		gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel statsPanel = createStatsPanel();
        contentPanel.add(statsPanel, gbc);

		//pannello con i pulsanti
		JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        saveButton = new JButton("Salva");
        saveButton.addActionListener(e -> {
            saveChanges();
            if (onSave != null) onSave.run();
			setVisible(false);
        });
        
        cancelButton = new JButton("Annulla");
        cancelButton.addActionListener(e -> {
            if (onCancel != null) onCancel.run();
			setVisible(false);
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(buttonPanel, gbc);
        
        // Aggiungi il pannello al layout principale
        add(contentPanel, new GridBagConstraints());
    }

	private void createAvatarGallery() {
		avatarGallery = new JPanel(new GridLayout(2,4,AVATAR_PADDING,AVATAR_PADDING));
		avatarGallery.setBackground(Color.WHITE);
		avatarGallery.setBorder(BorderFactory.createLoweredBevelBorder());
		avatarGallery.setPreferredSize(new Dimension(
					4 * (AVATAR_SIZE + AVATAR_PADDING) + AVATAR_PADDING,
					2 * (AVATAR_SIZE + AVATAR_PADDING) + AVATAR_PADDING
					));
		for (String avatarFile : AVATAR_FILES) {
			JLabel avatarLabel = createAvatarLabel(avatarFile);
			avatarGallery.add(avatarLabel);
        }
	}

	private JLabel createAvatarLabel(String avatarFile){
		JLabel label = new JLabel();
		label.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		
		//carica l'immagine
		BufferedImage avatarImage = loadAvatarImage(avatarFile);
		if (avatarImage != null) {
			// Ridimensiona l'immagine
			Image scaledImage = avatarImage.getScaledInstance(AVATAR_SIZE, AVATAR_SIZE, Image.SCALE_SMOOTH);
			label.setIcon(new ImageIcon(scaledImage));
		} else {
			// fallback se l'immagine non si carica(odio i fallback)
			label.setText("Avatar");
			label.setOpaque(true);
			label.setBackground(Color.LIGHT_GRAY);
		}

		// Evidenzia se è l'avatar corrente
		updateAvatarSelection(label, avatarFile);

		// Listener per la selezione
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectAvatar(avatarFile);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (!avatarFile.equals(selectedAvatarPath)) {
					label.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (!avatarFile.equals(selectedAvatarPath)) {
					label.setBorder(null);
				}
			}
		});

		return label;
	}
	
    private BufferedImage loadAvatarImage(String filename) {
        String path = "/images/avatars/" + filename;
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is != null) {
                return ImageIO.read(is);
            } else {
                System.err.println("Avatar image not found: " + path);
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error loading avatar image: " + path);
            e.printStackTrace();
            return null;
        }
    }
	
    private void selectAvatar(String avatarFile) {
        selectedAvatarPath = avatarFile;
        
        // Aggiorna i bordi di tutti gli avatar
        for (Component comp : avatarGallery.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                // Trova quale avatar rappresenta questo label (semplificato)
                String fileName = findAvatarFileForLabel(label);
                updateAvatarSelection(label, fileName);
            }
        }
    }

    private void updateAvatarSelection(JLabel label, String avatarFile) {
        if (avatarFile.equals(selectedAvatarPath)) {
            label.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
        } else {
            label.setBorder(null);
        }
    }
    
    private String findAvatarFileForLabel(JLabel label) {
        // Metodo semplificato per associare il label al file
        // In un'implementazione più robusta, si potrebbe usare una Map
        Component[] components = avatarGallery.getComponents();
        for (int i = 0; i < components.length && i < AVATAR_FILES.length; i++) {
            if (components[i] == label) {
                return AVATAR_FILES[i];
            }
        }
        return "";
    }
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistiche"));
        
        statsPanel.add(new JLabel("Partite giocate:"));
        statsPanel.add(new JLabel(String.valueOf(profile.getGamesPlayed())));
        statsPanel.add(new JLabel("Partite vinte:"));
        statsPanel.add(new JLabel(String.valueOf(profile.getGamesWon())));
        
        return statsPanel;
    }
    
    private void saveChanges() {
        String newNickname = nicknameField.getText().trim();
        if (!newNickname.isEmpty()) {
            profile.setNickname(newNickname);
        }
        
        // Salva il path dell'avatar selezionato
        if (!selectedAvatarPath.isEmpty()) {
            // Salviamo solo il nome del file, non il path completo
            profile.setAvatarPath(selectedAvatarPath);
        }
    }
    
    public void setOnSave(Runnable callback) {
        this.onSave = callback;
    }
    
    public void setOnCancel(Runnable callback) {
        this.onCancel = callback;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Disegna lo sfondo semi-trasparente
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }
}
