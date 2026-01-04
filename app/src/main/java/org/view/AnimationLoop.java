package org.view;

import javax.swing.*;
import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;

/**
 * Gestisce il ciclo di animazione in un thread separato implementando Runnable
 */
public class AnimationLoop implements Runnable {
    // fps dell'animazione
    private final int FPS = 60;
    // tempo target per frame in nanosecondi (~16.67 ms)
    private final long TARGET_TIME_NS = 1_000_000_000L / FPS;

    private volatile boolean isRunning = false;
    private Thread gameThread;
    
    // set di oggetti che implementano Animatable e sono in animazione
    private final Set<Animatable> animatedObjects = Collections.synchronizedSet(new HashSet<>());

    // flag per cercare di evitare repaint inutili
    private volatile boolean repaintScheduled = false;
    
    // componente da ridisegnare 
    private volatile JComponent repaintTarget;

    /**
     * Imposta il componente che verrà ridisegnato ad ogni frame di animazione
     * @param target Il componente da ridisegnare (es. TablePanel)
     */
    public void setRepaintTarget(JComponent target) {
        this.repaintTarget = target;
    }

    public void start() {
        if (!isRunning) {
            isRunning = true;
            gameThread = new Thread(this, "AnimationThread");
            gameThread.start();
        }
    }

    public void stop() {
        this.isRunning = false;
        if (gameThread != null) {
            try {
                gameThread.join(2000); // Timeout di 2 secondi
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * aggiunge un oggetto animabile al ciclo di animazione
     * @param obj Animatable
     */
    public void addAnimatable(Animatable obj) {
        animatedObjects.add(obj);
    }

    /**
     * elimina un oggetto animatable dal ciclo di animazione
     * @param obj l'oggetto da rimuovere
     */
    public void removeAnimatable(Animatable obj) {
        animatedObjects.remove(obj);
    }

    /**
     * Verifica se ci sono animazioni in corso
     * @return true se almeno un oggetto sta animando
     */
    public boolean hasActiveAnimations() {
        return !animatedObjects.isEmpty();
    }

    @Override
    public void run() {
        long lastLoopTime = System.nanoTime();
        
        while (isRunning) {
            long now = System.nanoTime();
            double deltaTime = (now - lastLoopTime) / 1_000_000_000.0;
            lastLoopTime = now;
            
            boolean anyAnimating = false;
            
            // creo una copia per iterare in sicurezza
            List<Animatable> snapshot;
            synchronized (animatedObjects) {
                snapshot = new ArrayList<>(animatedObjects);
            }
            
            // Itero sulla copia 
            for (Animatable obj : snapshot) {
                obj.stepAnimation(deltaTime);
                
                if (!obj.isAnimating()) {
                    // Rimuovi in modo thread-safe
                    animatedObjects.remove(obj);
                } else {
                    anyAnimating = true;
                }
            }
            
            // repaint solo se necessario
            if (anyAnimating && repaintTarget != null && !repaintScheduled) {
                repaintScheduled = true;
                SwingUtilities.invokeLater(() -> {
                    if (repaintTarget != null) {
                        repaintTarget.repaint();
                    }
                    repaintScheduled = false;
                });
            }
            
            long timeTaken = System.nanoTime() - now;
            long sleepTimeNS = TARGET_TIME_NS - timeTaken;
            
            if (sleepTimeNS > 0) {
                long sleepTimeMS = sleepTimeNS / 1_000_000;
                int remainingNanos = (int) (sleepTimeNS % 1_000_000);
                try {
                    Thread.sleep(sleepTimeMS, remainingNanos);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
