package org.util;

public class ModelEventMessage {

	private final ModelEvent event;
	private final Object payload;

	public ModelEventMessage(ModelEvent event, Object payload){
		this.event = event;
		this.payload = payload;
	}

	//metodi getters

	public ModelEvent getEvent(){ return event;}
	public Object getPayload(){ return payload;}

	@Override

	public String toString(){
		return "ModelEventMessage:{" + event + ",payload = " + payload + "}";

	}
	/**
	 * tipi di evento emessi dal model
	 */

	public enum ModelEvent{
		CARDS_DEALT,
		CARD_PLAYED,
		TURN_STARTED,
		TRICK_STARTED,
		TRICK_ENDED,
		ROUND_STARTED,
		ROUND_ENDED,
		GAME_STARTED,
		GAME_OVER,
		PROFILE_CHANGED,
		GAME_STATE_UPDATE,
	}
}
