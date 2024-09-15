package com.brahvim.agc.server.back;

import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.brahvim.agc.server.event.EventDispatchThread;

public class Backend {

	// Class init bugs if this ain't up the top here -_-:
	private static final AtomicBoolean BOOL_ATOMIC_EDT_SHUTDOWN_NOTIFIER = new AtomicBoolean();

	public static final AtomicInteger INT_CLIENTS_LEFT = new AtomicInteger();

	public static final EventDispatchThread EDT = new EventDispatchThread(

			Backend.BOOL_ATOMIC_EDT_SHUTDOWN_NOTIFIER,
			"ACD:EDT:BACKEND"

	);

	static ServerSocket socketForWelcome;
	static Thread threadForWelcomeSocket;

	private Backend() {
		throw new IllegalAccessError();
	}

	public static void shutdown() {
		Backend.BOOL_ATOMIC_EDT_SHUTDOWN_NOTIFIER.set(true);

		try {
			Backend.EDT.join();
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
