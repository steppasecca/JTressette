package org.view;

import org.model.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * classe che mostra l'immagine della carta 
 */
public class CardView extends JComponent implements Animatable {
    private static final long serialVersionUID = 1L;
    
    private Card card;
    private BufferedImage image;

    // riferimento al loop di animazione
    private final AnimationLoop animationLoop;

    // Coordinate attuali durante l'animazione (volatile per visibilità tra thread)
    private volatile int currentX;
    private volatile int currentY;
    
    // informazioni sullo stato dell'animazione
    private Point startLocation;
    private Point targetLocation;
    private Runnable onComplete;
    private volatile boolean isAnimating = false;
    private long startTime;

    // costante per la durata dell'animazione
    private final double ANIMATION_DURATION = 0.5;

    public CardView(Card card, AnimationLoop animationLoop) {
        this.card = card;
        if (card != null) this.image = ImageCache.getImageForCard(card);
        setSize(80, 120);
        this.animationLoop = animationLoop;
        
        // Inizializza le coordinate correnti
        this.currentX = getX();
        this.currentY = getY();
    }

    /**
     * setter per la carta card
     * @param card 
     */
    public void setCard(Card card) {
        this.card = card;
        this.image = (card == null) ? null : ImageCache.getImageForCard(card);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // otteniamo le dimensioni del componente
        int w = getWidth();
        int h = getHeight();
        
        // create() per evitare conflitti con altre componenti grafiche
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            // anti-aliasing/quality hints per migliorare la nitidezza
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            if (image != null) {
                // se c'è un'immagine la ridimensiona e la centra
                int iw = image.getWidth();
                int ih = image.getHeight();
                double scale = Math.min((double) w / iw, (double) h / ih);
                int dw = (int) (iw * scale);
                int dh = (int) (ih * scale);
                int dx = (w - dw) / 2;
                int dy = (h - dh) / 2;
                g2.drawImage(image, dx, dy, dw, dh, null);
            } else {
                // se non c'è un'immagine viene creato un semplice posto dove scrivere il nome
                g2.setColor(new Color(220, 220, 220));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 8, 8);
                g2.setColor(Color.DARK_GRAY);
                g2.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
                String txt = (card == null) ? "—" : card.getRank().name().toLowerCase() + " " + card.getSuit().name().toLowerCase();
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(txt);
                g2.drawString(txt, Math.max(4, (w - tw) / 2), h / 2);
            }
        } finally {
            // dispose per il rilascio delle risorse
            g2.dispose();
        }
    }

    /**
     * Inizializza e avvia l'animazione al punto target specificato.
     * @param x Coordinata X di destinazione.
     * @param y Coordinata Y di destinazione.
     * @param onComplete Codice da eseguire al termine dell'animazione.
     */
    public void animateTo(int x, int y, Runnable onComplete) {
        // Se c'è un'animazione in corso, la interrompiamo e ne avviamo una nuova.
        if (isAnimating) {
            animationLoop.removeAnimatable(this);
        }

        // Impostazione dello stato
        this.startLocation = new Point(currentX, currentY);
        this.targetLocation = new Point(x, y);
        this.onComplete = onComplete;
        this.startTime = System.currentTimeMillis();
        this.isAnimating = true;

        // Passa sé stesso (this) al thread di animazione per l'aggiornamento
        this.animationLoop.addAnimatable(this);
    }

    @Override
    public boolean isAnimating() {
        return isAnimating;
    }

    /**
     * Esegue un singolo passo di animazione. Chiamato dall'AnimationLoop 60 volte al secondo.
     * IMPORTANTE: Non chiama più invokeLater per ogni frame!
     * @param deltaTime Tempo trascorso dall'ultimo aggiornamento.
     */
    @Override
    public void stepAnimation(double deltaTime) {
        if (!isAnimating) return;

        // Calcola il tempo trascorso in secondi
        double elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0;

        // Calcola la percentuale di progresso (0.0 a 1.0), limitando a 1.0
        double progress = Math.min(1.0, elapsedTime / ANIMATION_DURATION);

        if (progress >= 1.0) {
            // --- Animazione Completata ---
            
            // Aggiorna le coordinate finali
            currentX = targetLocation.x;
            currentY = targetLocation.y;
            isAnimating = false;

            // Imposta la posizione finale SUL EDT una sola volta
            SwingUtilities.invokeLater(() -> {
                setLocation(currentX, currentY);
                // Non serve repaint qui, verrà fatto dall'AnimationLoop
                
                // Esegue la callback
                if (onComplete != null) {
                    onComplete.run();
                }
            });

            return;
        }

        // --- Calcolo della Posizione Intermedia ---
        
        // Funzione di easing lineare
        currentX = (int) (startLocation.x + (targetLocation.x - startLocation.x) * progress);
        currentY = (int) (startLocation.y + (targetLocation.y - startLocation.y) * progress);

        // NON chiamiamo più invokeLater qui!
        // L'AnimationLoop farà UN SOLO repaint sul container
    }

    /**
     * Override di getX per usare currentX durante l'animazione
     */
    @Override
    public int getX() {
        return isAnimating ? currentX : super.getX();
    }

    /**
     * Override di getY per usare currentY durante l'animazione
     */
    @Override
    public int getY() {
        return isAnimating ? currentY : super.getY();
    }

    /**
     * Override di setLocation per aggiornare anche currentX/currentY
     */
    @Override
    public void setLocation(int x, int y) {
        this.currentX = x;
        this.currentY = y;
        super.setLocation(x, y);
    }

    /**
     * Override di setLocation(Point) per consistenza
     */
    @Override
    public void setLocation(Point p) {
        setLocation(p.x, p.y);
    }

    /**
     * Override di getBounds per usare le coordinate animate
     */
    @Override
    public Rectangle getBounds() {
        if (isAnimating) {
            return new Rectangle(currentX, currentY, getWidth(), getHeight());
        }
        return super.getBounds();
    }
}
