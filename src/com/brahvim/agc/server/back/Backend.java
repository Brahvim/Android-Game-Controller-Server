package com.brahvim.agc.server.back;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ssl.SSLServerSocketFactory;

import com.brahvim.agc.server.App;
import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.front.FrontendNotification;

public class Backend {

	public static void launch() {
		BackendNotification.START_BACKEND.waitForFireAndHandleInterrupts();

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
			return null;
		}
	}

}
