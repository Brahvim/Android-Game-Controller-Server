package com.brahvim.agc.server.back;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLServerSocketFactory;

import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.event.Event;
import com.brahvim.agc.server.event.EventType;
import com.brahvim.agc.server.front.StageHome;

public final class EventAwaitOneClient {

	public static final EventType EVENT_TYPE = EventAwaitOneClient::handle;

	private EventAwaitOneClient() {
		throw new IllegalAccessError();
	}

	public static Event create() {
		return new Event(EventAwaitOneClient.EVENT_TYPE);
	}

	private static void socketThreadCallee() {
		// System.out.println("Awaiting one client...");
		final var socket = Backend.socketForWelcome =
				/* */ Backend.socketForWelcome == null
						? EventAwaitOneClient.createSslServerSocket()
						: Backend.socketForWelcome;

		// System.out.println("Entering `socket::accept()` loop...");

		for (int i = 0; (i = Backend.INT_CLIENTS_LEFT.get()) > 0;)
			try {
				System.out.printf("Iterated `socket::accept()` loop for client `%d`...%n", i);
				// final Socket clientSocket =
				socket.accept();
				Backend.socketForWelcome.close();
				Backend.INT_CLIENTS_LEFT.getAndDecrement();
			} catch (final IOException e) {
				if (e instanceof SocketTimeoutException) { // NOSONAR! Not again!
					// System.err.println("Socket timed out.");
					break;
					// App.exit(ExitCode.WELCOME_SOCKET_TIMEOUT);
				}
			} catch (final SecurityException e) {
				StageHome.exit(ExitCode.SSL_SOCKET_ACCEPT_PERMISSION);
			}

		Backend.threadForWelcomeSocket = null;
		// System.out.println("Welcome socket thread stopped.");
	}

	private static void handle(final Event p_event) {
		if (Backend.threadForWelcomeSocket != null) {
			// System.out.println("Welcome socket thread told to wait for another client.");
			return;
		}

		final var thread = Backend.threadForWelcomeSocket = new Thread(

				EventAwaitOneClient::socketThreadCallee,
				"AGC:WELCOME_SOCKET"

		);

		thread.start();
		// System.out.println("Welcome socket thread created.");
	}

	private static ServerSocket createSslServerSocket() {
		try {
			final var socket = SSLServerSocketFactory.getDefault().createServerSocket(19132);
			socket.setSoTimeout(5);
			return socket;
		} catch (final IOException e) {
			StageHome.exit(ExitCode.WELCOME_SOCKET_PORT_UNAVAILABLE);
		} catch (final SecurityException e) {
			StageHome.exit(ExitCode.SSL_SOCKET_CREATION_PERMISSION);
		}

		return null;
	}

}
