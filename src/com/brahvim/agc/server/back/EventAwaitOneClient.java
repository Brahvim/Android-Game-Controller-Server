package com.brahvim.agc.server.back;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.brahvim.agc.server.App;
import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.event.Event;
import com.brahvim.agc.server.event.EventType;

public final class EventAwaitOneClient {

	public static final EventType EVENT_TYPE = EventAwaitOneClient::handle;

	public static Event create() {
		return new Event(EventAwaitOneClient.EVENT_TYPE);
	}

	private static void threadCallee() {
		System.out.println("Awaiting one client...");

		final var socket = Backend.welcomeSocket = Backend.createSslServerSocket();

		try {
			final Socket clientSocket = socket.accept();

			// Compare against some blacklist here!
			// client.getInetAddress().getHostAddress();

			// Backend.clientSslSockets.add(client);
		} catch (final IOException e) {
			if (e instanceof SocketTimeoutException) // NOSONAR! Not again!
				App.exit(ExitCode.WELCOME_SOCKET_TIMEOUT);
		} catch (final SecurityException e) {
			App.exit(ExitCode.SSL_SOCKET_ACCEPT_PERMISSION);
		}
	}

	private static void handle(final Event p_event) {
		final var thread = Backend.welcomeSocketThread = new Thread(

				null,
				EventAwaitOneClient::threadCallee,
				"AGC:WELCOME_SOCKET"

		);

		thread.start();
		System.out.println("Welcome socket created.");
	}

}
