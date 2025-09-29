package org.view;

/**
 * interfaccia per gli oggetti animabili
 */

public interface Animatable { 

	/**
     * Esegue un passo di animazione basato sul tempo trascorso.
     * Deve essere chiamato dal thread di animazione (fuori dalla EDT).
     * @param deltaTime Tempo trascorso dall'ultimo frame in secondi.
     */
    void stepAnimation(double deltaTime);
    
    /**
     * Indica se l'oggetto è ancora in animazione.
     * @return true se l'animazione è in corso, false altrimenti.
     */
    boolean isAnimating();
}
