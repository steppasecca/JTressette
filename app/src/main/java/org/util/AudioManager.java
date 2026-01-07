package org.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioManager {

    private static AudioManager instance;
    private Clip backgroundClip;

    public static AudioManager getInstance() {
        if (instance == null)
            instance = new AudioManager();
        return instance;
    }

    private AudioManager() {
        // costruttore privato per singleton
    }

    /**
     * Avvia la musica di sottofondo in loop continuo
     * @param filename nome del file audio 
     */
    public void playBackground(String filename) {
        stopBackground(); // nel caso stia già suonando
        try {
            String path = "/music/" + filename;
            System.out.println("[AudioManager] Caricamento: " + path);
            
            // Carica lo stream dalle risorse
            InputStream resourceStream = getClass().getResourceAsStream(path);
            if (resourceStream == null) {
                System.err.println("[AudioManager] File audio non trovato: " + path);
                return;
            }
            
            // carica tutto in memoria per garantire il loop
            byte[] audioBytes = loadAudioBytes(resourceStream);
            resourceStream.close();
            
            // Crea un nuovo stream dalla memoria
            ByteArrayInputStream byteStream = new ByteArrayInputStream(audioBytes);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(byteStream));
            
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioIn);
            
            
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); // musica infinita
            backgroundClip.start();
            
            
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            System.err.println("[AudioManager] Errore nel caricare la musica:");
            e.printStackTrace();
        }
    }
    
    /**
     * Carica l'intero file audio in un array di byte
     */
    private byte[] loadAudioBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] temp = new byte[4096];
        int bytesRead;
        
        while ((bytesRead = inputStream.read(temp)) != -1) {
            buffer.write(temp, 0, bytesRead);
        }
        
        return buffer.toByteArray();
    }

    /**
     * Ferma la musica di sottofondo
     */
    public void stopBackground() {
        if (backgroundClip != null) {
            if (backgroundClip.isRunning()) {
                backgroundClip.stop();
            }
            backgroundClip.close();
            backgroundClip = null;
            System.out.println("[AudioManager] Musica fermata");
        }
    }

    /**
     * Verifica se la musica è attiva
     */
    public boolean isPlaying() {
        return backgroundClip != null && backgroundClip.isRunning();
    }

    /**
     * Mette in pausa senza rilasciare la clip
     */
    public void pauseBackground() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            System.out.println("[AudioManager] Musica in pausa");
        }
    }

    /**
     * Riprende dopo la pausa
     */
    public void resumeBackground() {
        if (backgroundClip != null && !backgroundClip.isRunning()) {
            backgroundClip.start();
            System.out.println("[AudioManager] Musica ripresa");
        }
    }
}
