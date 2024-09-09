package com.brahvim.agc.server;

import com.brahvim.agc.server.event.Event;
import com.brahvim.agc.server.event.EventHandler;
import com.brahvim.agc.server.event.EventType;

public class TestEvent implements Event {

	public static final EventType TYPE = TestEvent::handle;

	@SuppressWarnings("unchecked")
	private static EventHandler<TestEvent>[] handlers = new EventHandler[0];

	public final String message;

	public TestEvent(final String p_message) {
		this.message = p_message;
	}

	@SuppressWarnings("unchecked")
	public static void registerHandlers(final EventHandler<TestEvent>... p_callbacks) {
		synchronized (TestEvent.handlers) {
			final EventHandler<TestEvent>[] freshArray // Fresh out of RAM, which *is* as hot as ovens these days.
					= new EventHandler[p_callbacks.length + TestEvent.handlers.length];
			System.arraycopy(p_callbacks, 0, freshArray, TestEvent.handlers.length, p_callbacks.length);
			TestEvent.handlers = freshArray;
		}
	}

	private static void handle(final Event p_event) {
		synchronized (TestEvent.handlers) {
			for (final var h : TestEvent.handlers)
				h.handle((TestEvent) p_event);
		}
	}

	@Override
	public EventType getType() {
		return TestEvent.TYPE;
	}

}
