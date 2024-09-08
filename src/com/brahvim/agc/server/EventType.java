package com.brahvim.agc.server;

@FunctionalInterface
public interface EventType {

	public void /* synchronized */ handle(final Event p_event);

}
