package org.view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache semplice per le immagini delle carte (carica da /cards/<filename>).
 */
public final class ImageCache {
    private static final Map<String, BufferedImage> CACHE = new ConcurrentHashMap<>();

    private ImageCache() {}

    public static BufferedImage getImage(String filename) {
        if (filename == null) return null;
        return CACHE.computeIfAbsent(filename, ImageCache::loadImageFromResources);
    }

    public static BufferedImage getImageForCard(org.model.Card card) {
        if (card == null) return null;
        String fname = CardImageMapper.filenameFor(card);
        return getImage(fname);
    }

    private static BufferedImage loadImageFromResources(String filename) {
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
