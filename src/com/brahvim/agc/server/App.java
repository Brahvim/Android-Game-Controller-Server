package com.brahvim.agc.server;

import com.brahvim.agc.server.back.Client;
import com.brahvim.agc.server.front.JavaFxApp;

import javafx.application.Application;

public final class App {

	private App() {
		throw new IllegalAccessError();
	}

	public static void main(final String... p_args) {
		// PS Remember to *somehow get these arguments to the JVM* for JavaFX:
		// `--module-path ./lib/openjfx --add-modules javafx.controls,javafx.fxml`
		// (I don't really need the `javafx.fxml` module for this app, but anyway.)

		final int numClients = 50_000;
		final Client[] clients = new Client[numClients];

		for (int i = 0; i < numClients; ++i) {
			System.out.println(i);
			final Client client = new Client();
			client.setUdpSocketThread(Thread.currentThread());
			System.out.println(client.getUdpSocketThread()); // .getName());
			clients[i] = client;
		}

		for (final Client c : clients)
			c.destroy();

		new Thread(null, App::launchFxApp, "AGC:FX_APP_LAUNCHER").start();
	}

	public static void exit(final ExitCode p_exitCode) {
		System.exit(p_exitCode.ordinal());
	}

	private static void launchFxApp() {
		Application.launch(JavaFxApp.class);
	}

}
