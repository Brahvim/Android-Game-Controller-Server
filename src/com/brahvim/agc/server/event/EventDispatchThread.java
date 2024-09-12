package com.brahvim.agc.server.event;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class EventDispatchThread {

	private final Thread thread;
	private final Queue<Event> queue = new ConcurrentLinkedQueue<>();
	private final Object queueNoLongerEmptyNotifierLock = new Object();

	public EventDispatchThread(final String p_upperCaseThreadName) {
		this.thread = new Thread(

				this::edtLoop,
				"AGC:EDT:".concat(p_upperCaseThreadName)

		);

		this.thread.start();
	}

	public void publish(final Event... p_events) {
		for (final Event e : p_events)
			if (e != null)
				this.queue.add(e);

		synchronized (this.queueNoLongerEmptyNotifierLock) {
			this.queueNoLongerEmptyNotifierLock.notifyAll();
		}
	}

	private void edtLoop() {
		while (true) {
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
