package com.brahvim.agc.server;

import com.brahvim.agc.server.back.AgcBackend;
import com.brahvim.agc.server.front.JavaFxApp;

import javafx.application.Application;

public class App {

	public static void main(final String[] p_args) {
		// PS Remember to *somehow get these arguments to the JVM for JavaFX:
		// `--module-path ./lib/openjfx --add-modules javafx.controls,javafx.fxml`
		new Thread(() -> Application.launch(JavaFxApp.class)).start();
		AgcBackend.launch();
	}

}
