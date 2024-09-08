package com.brahvim.agc.server.event;

@FunctionalInterface
public interface EventHandler<EventT extends Event> {

	public void handle(final EventT p_event);

}
