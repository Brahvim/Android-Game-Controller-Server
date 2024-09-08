package com.brahvim.agc.server.test_event;

import com.brahvim.agc.server.Event;

public class TestEvent implements Event {

	public static final class Type implements Event.Type {

		@Override
		public void handle(final Event p_event) {
			// Slow!:
			// if (!(p_event instanceof final TestEvent event))
			// return;

			// Slow enough to let me make this method a part of `TestEvent` instead!
			// ...Then insert a reference to it and not call Event.Type::Type()` down there!

			if (p_event.getType() != TestEvent.TYPE)
				return;

			final TestEvent event = (TestEvent) p_event;
			System.out.println(event.message);
		}

	}

	public static final Event.Type TYPE = new Type();

	protected String message;

	public TestEvent(final String p_message) {
		this.message = p_message;
	}

	@Override
	public Event.Type getType() {
		return TestEvent.TYPE;
	}

}
