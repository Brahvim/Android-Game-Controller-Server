package com.brahvim.agc.server;

import com.brahvim.agc.server.back.Backend;
import com.brahvim.agc.server.front.JavaFxApp;
import com.brahvim.agc.server.test_event.TestEvent;

import javafx.application.Application;

public class App {

	@SuppressWarnings("unchecked")
	public static void main(final String[] p_args) {
		// PS Remember to *somehow get these arguments to the JVM* for JavaFX:
		// `--module-path ./lib/openjfx --add-modules javafx.controls,javafx.fxml`
		// (I don't really need the `javafx.fxml` module for this app, but anyway.)
		final var thread = new Thread() {

			@Override
			public void run() {
				Application.launch(JavaFxApp.class);
			}

		};

		thread.setName("AGC:FX_APP_LAUNCHER");
		thread.start();

		TestEvent.registerHandlers(p_event -> System.out.println(p_event.message));

		new Thread() {

			@Override
			public void run() {
				for (int i = 0; i < 500; i++)
					DefaultEdt.publish(new TestEvent("" + i));
			}

		}.start();

		new Thread() {

			@Override
			public void run() {
				for (int i = 0; i < 500; i++)
					DefaultEdt.publish(new TestEvent("" + i));
			}

		}.start();

		Backend.launch();
	}

	public static void exit(final ExitCode p_exitCode) {
		System.exit(p_exitCode.ordinal());
	}

}
