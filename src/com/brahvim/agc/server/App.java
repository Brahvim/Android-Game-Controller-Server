package com.brahvim.agc.server;

import com.brahvim.agc.server.back.Backend;
import com.brahvim.agc.server.front.JavaFxApp;

import javafx.application.Application;

public class App {

	public static void main(final String[] p_args) {
		// PS Remember to *somehow get these arguments to the JVM for JavaFX:
		// `--module-path ./lib/openjfx --add-modules javafx.controls,javafx.fxml`
		final var thread = new Thread() {

			@Override
			public void run() {
				Application.launch(JavaFxApp.class);
			}

		};

		thread.setName("AGC:JAVA_FX_APP");
		thread.start();

		Backend.launch();
	}

	public static void exit(final ExitCode p_exitCode) {
		System.exit(p_exitCode.ordinal());
	}

}
