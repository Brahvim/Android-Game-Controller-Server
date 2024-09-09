package com.brahvim.agc.server.event;

public class Event {

	// The C version:
	// public final class Event {
	// public final long ID;

	public final EventType TYPE;

	public Event(final EventType p_type) {
		// We don't use `Objects::requireNonNull()` around here, chief.
		if (p_type == null)
			throw new NullPointerException("For `p_type` passed to `Event::Event(EventType p_type)`.");

		this.TYPE = p_type;
	}

}
