package com.brahvim.agc.server.event;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class EventDispatchThread {

	// region Fields.
	private final Thread thread;
	private final AtomicBoolean boolAtomicShutdownNotifier;
	private final Object queueNoLongerEmptyNotifierLock = new Object();
	private final ConcurrentLinkedQueue<Event> queue = new ConcurrentLinkedQueue<>();

	private static AtomicInteger count = new AtomicInteger();
	// endregion

	public EventDispatchThread(final AtomicBoolean p_shutdownNotifier, final String p_upperCaseThreadName) {
		EventDispatchThread.count.getAndIncrement();
		this.boolAtomicShutdownNotifier = p_shutdownNotifier;

		// Would save a lot of resources if this was lazy-initialized, but that's too
		// much checking for `EventDispatchThread::publish()`!...:

		this.thread = new Thread(

				this::edtLoop,
				p_upperCaseThreadName

		);

		this.thread.start();
	}

	public final void publish(final Event... p_events) {
		for (final Event e : p_events)
			if (e != null)
				this.queue.add(e);

		synchronized (this.queueNoLongerEmptyNotifierLock) {
			this.queueNoLongerEmptyNotifierLock.notifyAll();
		}
	}

	private final void edtLoop() {
		while (!this.boolAtomicShutdownNotifier.get()) {
			final Event event;

			synchronized (this.queueNoLongerEmptyNotifierLock) {
				while (this.queue.isEmpty())
					try {
						this.queueNoLongerEmptyNotifierLock.wait();
					} catch (final InterruptedException e) {
						this.thread.interrupt();
					}

				event = this.queue.poll();
			}

			// No longer hit thanks to `queueNoLongerEmptyNotifierLock`.
			if (event == null)
				continue;
			// ...But still could be if the event object registers just *is* empty :/
			// ...Which I *do avoid* as you'd see in `DefaultEdt::publish()` here.

			synchronized (event) {
				event.TYPE.handle(event);
			}
		}
	}

}
