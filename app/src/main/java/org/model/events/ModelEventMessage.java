package org.events;

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
}
