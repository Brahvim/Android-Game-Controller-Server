package com.brahvim.agc.server;

import com.brahvim.agc.server.back.Backend;
import com.brahvim.agc.server.event.DefaultEdt;
import com.brahvim.agc.server.front.JavaFxApp;

import javafx.application.Application;

public class App {

	@SuppressWarnings("unchecked")
	public static void main(final String[] p_args) {
		// PS Remember to *somehow get these arguments to the JVM* for JavaFX:
		// `--module-path ./lib/openjfx --add-modules javafx.controls,javafx.fxml`
		// (I don't really need the `javafx.fxml` module for this app, but anyway.)
		new Thread("AGC:FX_APP_LAUNCHER") {

			@Override
			public void run() {
				Application.launch(JavaFxApp.class);
			}

		}.start();

		TestEvent.registerHandlers(p_event -> System.out.println(p_event.message));

		DefaultEdt.publish(new TestEvent("1"));
		DefaultEdt.publish(new TestEvent("2"));

		Backend.launch();
	}

	public static void exit(final ExitCode p_exitCode) {
		System.exit(p_exitCode.ordinal());
	}

}
