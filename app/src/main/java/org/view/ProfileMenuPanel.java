//TODO controllare bene la classe se si può rimuovere qualcosa
//e se viola MVC
package org.view;

import org.model.UserProfile;
import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;

/**
 * Pannello opzioni per la modifica dei dati del proprio Profilo
 */
public class ProfileMenuPanel extends JPanel {
    private final UserProfile profile; //riferimento alle impostazioni utente
    private JTextField nicknameField; //campo per il nick
    private JPanel avatarGallery; // pannello per la visualizzazione degli avatar
    private JButton saveButton; //bottone per salvare
    private JButton cancelButton; //bottone per non salvare le modifiche
    private String selectedAvatarPath; //stringa che registra conto del path selezionato

    private Runnable onSave; //callback per il controller
    private Runnable onCancel; //callback per il controller

	//nome degli avatar
    private static final String[] AVATAR_FILES = {
        "avatar1.png", "avatar2.png", "avatar3.png", "avatar4.png",
    };

	//grandezza dell'avatar e spazio tra gli avatar (forse si può eliminare)
    private final int AVATAR_SIZE = 64;
    private final int AVATAR_PADDING = 8;

    public ProfileMenuPanel(UserProfile profile) {
        this.profile = profile;
        this.selectedAvatarPath = profile.getAvatarPath();
        setLayout(new GridBagLayout());
        setOpaque(false);
        initComponents();
    }

	/**
	 * metodo di utilità che setta inizializza i vari componenti
	 */
    private void initComponents() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(240, 240, 240));
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Titolo
        JLabel titleLabel = new JLabel("Opzioni profilo");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(titleLabel, gbc);

        // Campo nickname
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(new JLabel("Nickname"), gbc);

        nicknameField = new JTextField(profile.getNickname(), 20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(nicknameField, gbc);

        // Campo avatar
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel avatarLabel = new JLabel("Scegli Avatar");
        avatarLabel.setFont(avatarLabel.getFont().deriveFont(Font.BOLD));
        contentPanel.add(avatarLabel, gbc);

        // Galleria avatar
        createAvatarGallery();
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(new JScrollPane(avatarGallery), gbc);

        // Statistiche
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel statsPanel = createStatsPanel();
        contentPanel.add(statsPanel, gbc);

        // Pulsanti
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        saveButton = new JButton("Salva");
        saveButton.addActionListener(e -> {
            if (onSave != null) onSave.run();
        });

        cancelButton = new JButton("Annulla");
        cancelButton.addActionListener(e -> {
            if (onCancel != null) onCancel.run();
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(buttonPanel, gbc);

        add(contentPanel, new GridBagConstraints());
    }

    private void createAvatarGallery() {
        avatarGallery = new JPanel(new GridLayout(2, 4, AVATAR_PADDING, AVATAR_PADDING));
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

    private JLabel createAvatarLabel(String avatarFile) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Carica l'immagine
        BufferedImage avatarImage = loadAvatarImage(avatarFile);
        if (avatarImage != null) {
            Image scaledImage = avatarImage.getScaledInstance(AVATAR_SIZE, AVATAR_SIZE, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImage));
        } else {
            label.setText("Avatar");
            label.setOpaque(true);
            label.setBackground(Color.LIGHT_GRAY);
        }

        // Evidenzia se è quello selezionato
        updateAvatarSelection(label, avatarFile);

        // Selezione al click
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectAvatar(avatarFile);
            }
        });

        return label;
    }

    private BufferedImage loadAvatarImage(String filename) {
        String path = "/images/avatar/" + filename;
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
        for (Component comp : avatarGallery.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
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

    // --- Metodi per il Controller ---
    public String getNicknameInput() {
        return nicknameField.getText().trim();
    }

    public String getSelectedAvatarPath() {
        return selectedAvatarPath;
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
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }
}
