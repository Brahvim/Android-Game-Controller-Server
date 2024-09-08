package com.brahvim.agc.server.test_event;

import java.util.ArrayList;

import com.brahvim.agc.server.Event;
import com.brahvim.agc.server.EventHandler;
import com.brahvim.agc.server.EventType;

public class TestEvent implements Event {

	public static final EventType TYPE = TestEvent::handle;
	private static final ArrayList<EventHandler<TestEvent>> handlers = new ArrayList<>();

	protected String message;

	public TestEvent(final String p_message) {
		this.message = p_message;
	}

	public static synchronized void registerHandler(final EventHandler<TestEvent> p_callback) {
		TestEvent.handlers.add(p_callback);
	}

	private static synchronized void handle(final Event p_event) {
		if (p_event.getType() != TestEvent.TYPE)
			return;

		final TestEvent event = (TestEvent) p_event;
		// System.out.println(event.message);

		for (final var h : TestEvent.handlers)
			h.handle(event);
	}

	@Override
	public EventType getType() {
		return TestEvent.TYPE;
	}

}
