package org.view;

import org.model.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CardView extends JComponent {
    private static final long serialVersionUID = 1L; //evita InvalidClassException
    private Card card;
    private BufferedImage image; // cached image for this card (from ImageCache)

    public CardView(Card card) {
        this.card = card;
        if (card != null) this.image = ImageCache.getImageForCard(card);
		setSize(80, 120);
    }

	/**
	 * setter per la carta card
	 * @param card 
	 * @return void
	 */
    public void setCard(Card card) {
        this.card = card;
        this.image = (card == null) ? null : ImageCache.getImageForCard(card);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

		//otteniamo le dimensioni del componente
        int w = getWidth();
        int h = getHeight();
		//create() per evitare conflitti con altre componenti grafiche
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            // anti-aliasing/quality hints per migliorare la nitidezza
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            if (image != null) {
			//se c'è un'immagine la ridimensiona e la centra
                int iw = image.getWidth();
                int ih = image.getHeight();
                double scale = Math.min((double) w / iw, (double) h / ih);
                int dw = (int) (iw * scale);
                int dh = (int) (ih * scale);
                int dx = (w - dw) / 2;
                int dy = (h - dh) / 2;
                g2.drawImage(image, dx, dy, dw, dh, null);
            } else {
				//se non c'è un'immagine viene creato un semplice posto dove scrivere il nome
                g2.setColor(new Color(220,220,220));
                g2.fillRoundRect(0,0,w-1,h-1,8,8);
                g2.setColor(Color.DARK_GRAY);
                g2.drawRoundRect(0,0,w-1,h-1,8,8);
                String txt = (card == null) ? "–" : card.getRank().name().toLowerCase() + " " + card.getSuit().name().toLowerCase();
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(txt);
                g2.drawString(txt, Math.max(4, (w-tw)/2), h/2);
            }
        } finally {
			//dispose per il rilascio delle risorse
            g2.dispose();
        }
    }
	
	/**
	 * metodo che produce un'animazione di una carta spostandola da un punto iniziale ad uno finale
	 * @param x //coordinate di partenza
	 * @param y
	 * @param targetX //coordinate finali
	 * @param targetY
	 * @param onComplete //Runnable per callback quando finisce l'animazione
	 */
	public void animateTo(int startX, int startY, int targetX, int targetY, Runnable onComplete) {
		int steps = 30;
		int delay = 15;

		double dx = (targetX - startX) / (double) steps;
		double dy = (targetY - startY) / (double) steps;

		final int[] currentStep = {0};
		final double[] pos = {startX, startY};

		Timer animTimer = new Timer(delay, e -> {
			pos[0] += dx;
			pos[1] += dy;
			setLocation((int) Math.round(pos[0]), (int) Math.round(pos[1]));
			repaint();

			currentStep[0]++;
			if (currentStep[0] >= steps) {
				setLocation(targetX, targetY);
				((Timer) e.getSource()).stop();
				if (onComplete != null) onComplete.run();
			}
		});
		animTimer.start();
	}
}
