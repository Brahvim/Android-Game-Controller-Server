package com.brahvim.agc.server;

import com.brahvim.agc.server.front.JavaFxApp;

import javafx.application.Application;

public final class App {

	public static final StringTable STRINGS = StringTable.tryCreating("./res/strings/AgcStringTable.ini");

	private App() {
		throw new IllegalAccessError();
	}

	public static void main(final String... p_args) {
		// PS Remember to *somehow get these arguments to the JVM* for JavaFX:
		// `--module-path ./lib/openjfx --add-modules javafx.controls,javafx.fxml`
		// (I don't really need the `javafx.fxml` module for this app, but anyway.)

		new Thread(App::launchFxApp, "AGC:FX_APP_LAUNCHER").start();
	}

	public static void exit(final ExitCode p_exitCode) {
		System.out.print(ExitCode.ERROR_MESSAGE_PREFIX);
		System.out.println(p_exitCode.errorMessage);
		System.exit(p_exitCode.ordinal());
	}

	private static void launchFxApp() {
		Application.launch(JavaFxApp.class);
	}

}
