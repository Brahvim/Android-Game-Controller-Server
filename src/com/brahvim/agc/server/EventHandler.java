package com.brahvim.agc.server;

public interface EventHandler<EventT extends Event> {

	public void handle(final EventT p_event);

}
