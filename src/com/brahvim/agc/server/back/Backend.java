package com.brahvim.agc.server.back;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.net.ssl.SSLServerSocketFactory;

import com.brahvim.agc.server.App;
import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.event.EventDispatchThread;

public class Backend {

	public static final EventDispatchThread EDT = new EventDispatchThread("BACKEND");

	static ArrayList<Socket> clientSockets;
	static ServerSocket welcomeSocket;
	static Thread welcomeSocketThread;

	private Backend() {
		throw new IllegalAccessError();
	}

	static ServerSocket createSslServerSocket() {
		try {
			return SSLServerSocketFactory.getDefault().createServerSocket(19132);
		} catch (final IOException e) {
			App.exit(ExitCode.WELCOME_SOCKET_PORT_UNAVAILABLE);
		} catch (final SecurityException e) {
			App.exit(ExitCode.SSL_SOCKET_CREATION_PERMISSION);
		}

		return null;
	}

}
