package org.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
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
     * @param filename percorso del file audio
     */
    public void playBackground(String filename) {
        stopBackground(); // nel caso stia già suonando
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filename));
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioIn);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); // musica infinita
            backgroundClip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ferma la musica di sottofondo
     */
    public void stopBackground() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
            backgroundClip = null;
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
        }
    }

    /**
     * Riprende dopo la pausa
     */
    public void resumeBackground() {
        if (backgroundClip != null && !backgroundClip.isRunning()) {
            backgroundClip.start();
        }
    }
}
