package com.brahvim.agc.server.event;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultEdt {

	private static final Queue<Event> queue = new ConcurrentLinkedQueue<>();
	private static final Object queueNoLongerEmptyNotifierLock = new Object();
	private static final Thread globalEdt = new Thread(null, DefaultEdt::edtLoop, "AGC:EDT:GLOBAL");

	static {
		DefaultEdt.globalEdt.start();
	}

	private DefaultEdt() {
		throw new IllegalAccessError();
	}

	public static void publish(final Event... p_events) {
		for (final Event e : p_events)
			DefaultEdt.queue.add(e);

		synchronized (DefaultEdt.queueNoLongerEmptyNotifierLock) {
			DefaultEdt.queueNoLongerEmptyNotifierLock.notifyAll();
		}
	}

	private static void edtLoop() {
		while (true) {
			final Event event;

			synchronized (DefaultEdt.queueNoLongerEmptyNotifierLock) {
				while (DefaultEdt.queue.isEmpty())
					try {
						DefaultEdt.queueNoLongerEmptyNotifierLock.wait();
					} catch (final InterruptedException e) {
						DefaultEdt.globalEdt.interrupt();
					}

				event = DefaultEdt.queue.poll();
			}

			// No longer hit thanks to `queueNoLongerEmptyNotifierLock`.
			if (event == null)
				continue;
			// ...But still could be if the event object registers just *is* empty :/
			// ...Which I *do avoid* as you'd see in `DefaultEdt::publish()` here.

			synchronized (event) {
				event.getType().handle(event);
			}
		}
	}

}
