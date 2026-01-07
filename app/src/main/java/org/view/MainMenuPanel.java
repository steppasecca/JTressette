package org.view;

import javax.swing.*;
import java.awt.*;

/**
 * classe che mostra il menu principale
 */
public class MainMenuPanel extends JPanel {

    private final JComboBox<String> modeSelector;

    private Runnable onStart;
    private Runnable onToggleOption;
    private Runnable onToggleStat;

    public MainMenuPanel() {
        super(new GridBagLayout());
        setBackground(new Color(20, 80, 55)); // verde carino
        
        // Pannello centrale semi-trasparente
        JPanel inner = new JPanel(new GridBagLayout());
        inner.setBackground(new Color(0, 0, 0, 180)); // nero semi-trasparente
        inner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(212, 175, 55), 4), // bordino dorato
            BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // titolo 
        JLabel title = new JLabel("JTressette", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 52));
        title.setForeground(new Color(212, 175, 55)); // Oro
        gbc.gridy = 0;
        inner.add(title, gbc);

        // Sottotitolo
        JLabel subtitle = new JLabel("Il miglior gioco di carte italiano", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.ITALIC, 15));
        subtitle.setForeground(new Color(200, 200, 200));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 25, 10);
        inner.add(subtitle, gbc);

        // bottoni
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JButton startButton = createStyledButton("🎮  Gioca!");
        gbc.gridy = 2;
        inner.add(startButton, gbc);

        JButton optionsProfileButton = createStyledButton("👤  Profilo");
        gbc.gridy = 3;
        inner.add(optionsProfileButton, gbc);

        JButton statButton = createStyledButton("📊  Statistiche");
        gbc.gridy = 4;
        inner.add(statButton, gbc);

        // modalità
        JLabel modeLabel = new JLabel("Modalità di gioco:", SwingConstants.CENTER);
        modeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        modeLabel.setForeground(new Color(212, 175, 55));
        gbc.gridy = 5;
        gbc.insets = new Insets(25, 10, 8, 10);
        inner.add(modeLabel, gbc);

        // Selettore modalità
        modeSelector = new JComboBox<>(new String[]{"2 Giocatori", "4 Giocatori"});
        modeSelector.setFont(new Font("SansSerif", Font.PLAIN, 14));
        modeSelector.setBackground(new Color(40, 100, 70));
        modeSelector.setForeground(Color.WHITE);
        modeSelector.setFocusable(false);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 10, 10, 10);
        inner.add(modeSelector, gbc);

        // Callback bottoni
        startButton.addActionListener(e -> {
            if (onStart != null) onStart.run();
        });

        optionsProfileButton.addActionListener(e -> {
            if (onToggleOption != null) onToggleOption.run();
        });

        statButton.addActionListener(e -> {
            if (onToggleStat != null) onToggleStat.run();
        });

        add(inner);
    }

    /**
     * Crea un bottone stilizzato
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(46, 125, 85)); // Verde scuro
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(280, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effetto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 160, 110)); // Verde più chiaro
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(46, 125, 85));
            }
        });
        
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // gradiiente per il verde dello sfondo per renderlo più figo
        Graphics2D g2d = (Graphics2D) g.create();
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(20, 80, 55),           // verde scuro in alto
            0, getHeight(), new Color(15, 60, 40)  // verde più scuro in basso
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }

    // Setter per i callback
    public void setOnStart(Runnable r) {
        this.onStart = r;
    }

    public void setOnToggleOption(Runnable r) {
        this.onToggleOption = r;
    }

    public void setOnToggleStat(Runnable r) {
        this.onToggleStat = r;
    }

    public String getSelectedMode() {
        return (String) modeSelector.getSelectedItem();
    }
}
