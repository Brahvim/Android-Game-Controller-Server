package com.brahvim.agc.server.back;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.net.ssl.SSLServerSocketFactory;

import com.brahvim.agc.server.App;
import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.front.FrontendNotification;

public class Backend {

	static ArrayList<Socket> clientSockets;

	public static void launch() {
		BackendNotification.START_BACKEND.waitForFireAndHandleInterrupts();
		final var socket = Backend.createSslServerSocket();

		final var thread = new Thread() {

			@Override
			public void run() {
				try {
					Backend.clientSockets.add(socket.accept());
					BackendNotification.CLIENT_JOINED.fire();
				} catch (final IOException e) {
					if (e instanceof SocketTimeoutException) // NOSONAR! Not again!
						App.exit(ExitCode.WELCOME_SOCKET_TIMEOUT);
				} catch (final SecurityException e) {
					App.exit(ExitCode.SSL_SOCKET_ACCEPT_PERMISSIONS);
				}
			}

		};

		thread.setName("AGC:WELCOME_SOCKET");
		thread.start();

		try {
			Thread.sleep(2000);
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		System.out.println("Backend ready!");
		FrontendNotification.BACKEND_STARTED.fire();
	}

	public static ServerSocket createSslServerSocket() {
		try {
			return SSLServerSocketFactory.getDefault().createServerSocket(19132);
		} catch (final IOException e) {
			// if (e instanceof final UnknownHostException uhe) { }
			App.exit(ExitCode.WELCOME_SOCKET_PORT_UNAVAILABLE);
		} catch (final SecurityException e) {
			App.exit(ExitCode.SSL_SOCKET_CREATION_PERMISSION);
		}

		return null;
	}

}
