package org.view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * classe di utilità semplice per caricare una sola volta le immagini delle carte 
 * (carica da images/cards/filename) per le carte. 
 * 
 */
public final class ImageCache {

	//ConcurrentHashMap per evitare problemi di concorrenza
    private static final Map<String, BufferedImage> CACHE = new ConcurrentHashMap<>();


    private ImageCache() {}

	/**
	 * ritorna l'immagine e la aggiunge alla cache
	 *
	 * @param filename 
	 * @return BufferdImage
	 */
    public static BufferedImage getImage(String filename) {
        if (filename == null) return null;
		//se non è nella cache carichiamo l'immagine e l'aggiungiamo
        return CACHE.computeIfAbsent(filename, ImageCache::loadImageFromResources);
    }

	/**
	 *  ritorna l'immagine e la salva nella cache
	 *
	 * @param Card card
	 * @return image BuffedImage
	 */
    public static BufferedImage getImageForCard(org.model.Card card) {
        if (card == null) return null;
        String fname = CardImageMapper.filenameFor(card);
        return getImage(fname);
    }

	/**
	 * metodo ausiliario che concretamente carica l'immagine
	 *
	 * @param filename
	 * @return image BufferedImage
	 */
    private static BufferedImage loadImageFromResources(String filename) {
		//path relativo a JTressette/app/src/main/resources
        String path = "/images/cards/" + filename;
        try (InputStream is = ImageCache.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Image not found on classpath: " + path);
                return null;
            }
            return ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
