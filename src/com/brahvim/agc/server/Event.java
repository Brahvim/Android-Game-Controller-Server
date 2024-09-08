package com.brahvim.agc.server;

@FunctionalInterface
public interface Event {

	public EventType getType();

}
