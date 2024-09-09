package com.brahvim.agc.server.back;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.brahvim.agc.server.App;
import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.event.Event;
import com.brahvim.agc.server.event.EventType;

public class WelcomeSockEvent implements Event {

	private static AtomicBoolean done = new AtomicBoolean();
	public static final EventType TYPE = WelcomeSockEvent::handle;

	@Override
	public EventType getType() {
		return WelcomeSockEvent.TYPE;
	}

	public static void handle(final Event p_event) {
		WelcomeSockEvent.done.set(true);
		final var socket = Backend.createSslServerSocket();

		final var thread = new Thread("AGC:WELCOME_SOCKET") {

			@Override
			public void run() {
				try {
					Backend.clientSockets.add(socket.accept());
				} catch (final IOException e) {
					if (e instanceof SocketTimeoutException) // NOSONAR! Not again!
						App.exit(ExitCode.WELCOME_SOCKET_TIMEOUT);
				} catch (final SecurityException e) {
					App.exit(ExitCode.SSL_SOCKET_ACCEPT_PERMISSIONS);
				}
			}

		};

		thread.start();
		System.out.println("Welcome socket created.");
	}

}
