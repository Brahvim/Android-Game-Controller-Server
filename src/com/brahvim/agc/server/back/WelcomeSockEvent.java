package com.brahvim.agc.server.back;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.brahvim.agc.server.App;
import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.event.Event;
import com.brahvim.agc.server.event.EventType;

public final class WelcomeSockEvent {

	public static final EventType EVENT_TYPE = WelcomeSockEvent::handle;

	private WelcomeSockEvent() {
		throw new IllegalAccessError();
	}

	public static Event create() {
		return new Event(WelcomeSockEvent.EVENT_TYPE);
	}

	private static void threadCallee() {
		final var socket = Backend.welcomeSocket = Backend.createSslServerSocket();

		try {
			final Socket client = socket.accept();

			// Compare against some blacklist here!
			// client.getInetAddress().getHostAddress();

			// Backend.clientSslSockets.add(client);
		} catch (final IOException e) {
			if (e instanceof SocketTimeoutException) // NOSONAR! Not again!
				App.exit(ExitCode.WELCOME_SOCKET_TIMEOUT);
		} catch (final SecurityException e) {
			App.exit(ExitCode.SSL_SOCKET_ACCEPT_PERMISSIONS);
		}
	}

	private static void handle(final Event p_event) {
		final var thread = Backend.welcomeSocketThread = new Thread(

				null,
				WelcomeSockEvent::threadCallee,
				"AGC:WELCOME_SOCKET"

		);

		thread.start();
		System.out.println("Welcome socket created.");
	}

}
