package com.brahvim.agc.server.back;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.net.ssl.SSLServerSocketFactory;

import com.brahvim.agc.server.App;
import com.brahvim.agc.server.ExitCode;

public class Backend {

	static ArrayList<Socket> clientSockets;

	private Backend() {
		throw new IllegalAccessError();
	}

	public static void launch() {
		try {
			Thread.sleep(2000);
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		System.out.println("Backend ready!");
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
