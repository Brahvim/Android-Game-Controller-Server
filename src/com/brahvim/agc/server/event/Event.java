package com.brahvim.agc.server.event;

@FunctionalInterface
public interface Event {

	public EventType getType();

}
