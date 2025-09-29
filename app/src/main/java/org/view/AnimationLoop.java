package org.view;

import javax.swing.*;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *Gestisce il ciclo di animazione in un thread separato implementa Runnable
 */

public class AnimationLoop implements Runnable{
	//fps dell'animazione
	private final int FPS = 60;
// Tempo target per frame in nanosecondi (~16.67 ms)
    private final long TARGET_TIME_NS = 1_000_000_000L / FPS;

	private volatile boolean isRunning = false;
	private Thread gameThread;
	// Set di oggetti che implementano Animatable e sono in animazione
	private final Set<Animatable> animatedObjects = Collections.newSetFromMap(new ConcurrentHashMap<>());

	public void start(){
		if(!isRunning){
			isRunning = true;
			gameThread = new Thread(this,"AnimationThread");
			gameThread.start();
		}
	}

	public void stop(){
		this.isRunning = false;
		if(gameThread!=null){
			try{
				gameThread.join();
			} catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}

		}
	}


	/**
	 * aggiunge un oggetto animabile al ciclo di animazione
	 *
	 * @param obj Animatable
	 */
	public void addAnimatable(Animatable obj){
		animatedObjects.add(obj);
	}

	/**
	 * elimina un oggetto animatable al ciclo di animazione
	 * @param obj l'oggeto da rimuovere
	 */
	public void removeAnimatable(Animatable obj){
		animatedObjects.remove(obj);
	}

	@Override
	public void run(){
		long lastLoopTime = System.nanoTime();
		while(isRunning){
			long now = System.nanoTime();
			double deltaTime = (now-lastLoopTime)/1_000_000_000.0;
			lastLoopTime = now;
			for(Animatable obj : animatedObjects){
				obj.stepAnimation(deltaTime);
				if(!obj.isAnimating()){
					removeAnimatable(obj);
				}
			}
			long timeTaken = System.nanoTime()-now;
			long sleepTimeNS = TARGET_TIME_NS-timeTaken;
			if(sleepTimeNS>0){
				//conversione in nanosecondi
				long sleepTimeMS = sleepTimeNS / 1_000_000;
                int remainingNanos = (int) (sleepTimeNS % 1_000_000);
				try{
					Thread.sleep(sleepTimeMS,remainingNanos);
				} catch(InterruptedException e){
					Thread.currentThread().interrupt();
				}
			}
		}
	}



}
