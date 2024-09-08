package com.brahvim.agc.server;

public interface Event {

	public static interface Type {

		public void handle(final Event p_event);

	}

	public Event.Type getType();

}
