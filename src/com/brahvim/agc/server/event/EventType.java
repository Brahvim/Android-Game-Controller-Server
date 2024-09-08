package com.brahvim.agc.server.event;

@FunctionalInterface
public interface EventType {

	public void /* synchronized */ handle(final Event p_event);

}
