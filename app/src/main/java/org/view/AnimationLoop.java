package org.view;

import javax.swing.*;
import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestisce il ciclo di animazione in un thread separato implementa Runnable
 */
public class AnimationLoop implements Runnable {
    // fps dell'animazione
    private final int FPS = 60;
    // Tempo target per frame in nanosecondi (~16.67 ms)
    private final long TARGET_TIME_NS = 1_000_000_000L / FPS;

    private volatile boolean isRunning = false;
    private Thread gameThread;
    
    // Set di oggetti che implementano Animatable e sono in animazione
    private final Set<Animatable> animatedObjects = Collections.newSetFromMap(new ConcurrentHashMap<>());
    
    // NUOVO: Componente da ridisegnare (tipicamente il TablePanel o GamePanel)
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
        List<Animatable> toRemove = new ArrayList<>();
        
        while (isRunning) {
            long now = System.nanoTime();
            double deltaTime = (now - lastLoopTime) / 1_000_000_000.0;
            lastLoopTime = now;
            
            // Pulisci la lista dei rimovibili
            toRemove.clear();
            boolean anyAnimating = false;
            
            // Aggiorna tutte le animazioni
            for (Animatable obj : animatedObjects) {
                obj.stepAnimation(deltaTime);
                
                if (!obj.isAnimating()) {
                    toRemove.add(obj);
                } else {
                    anyAnimating = true;
                }
            }
            
            // Rimuovi gli oggetti che hanno finito l'animazione
            // DOPO l'iterazione per evitare ConcurrentModificationException
            for (Animatable obj : toRemove) {
                animatedObjects.remove(obj);
            }
            
            // REPAINT CENTRALIZZATO: un solo repaint per frame
            // Solo se c'è almeno un'animazione attiva
            if (anyAnimating && repaintTarget != null) {
                SwingUtilities.invokeLater(() -> {
                    repaintTarget.repaint();
                });
            }
            
            // Calcola il tempo di sleep per mantenere gli FPS target
            long timeTaken = System.nanoTime() - now;
            long sleepTimeNS = TARGET_TIME_NS - timeTaken;
            
            if (sleepTimeNS > 0) {
                // conversione in millisecondi e nanosecondi
                long sleepTimeMS = sleepTimeNS / 1_000_000;
                int remainingNanos = (int) (sleepTimeNS % 1_000_000);
                try {
                    Thread.sleep(sleepTimeMS, remainingNanos);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break; // Esci dal loop se interrotto
                }
            }
            
            // Optional: Log performance warning se il frame è troppo lento
            if (timeTaken > TARGET_TIME_NS * 2) {
                System.err.println("[AnimationLoop] Warning: Frame took " + 
                    (timeTaken / 1_000_000.0) + "ms (target: " + 
                    (TARGET_TIME_NS / 1_000_000.0) + "ms), Objects: " + 
                    animatedObjects.size());
            }
        }
    }
}
