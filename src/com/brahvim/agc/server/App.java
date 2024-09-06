package com.brahvim.agc.server;

import javafx.application.Application;

public class App {

    public static void main(final String[] p_args) {
        Application.launch(JavaFxApp.class);
        // PS Remember to *somehow get these arguments to the JVM for JavaFX:
        // `--module-path ./lib/openjfx/ --add-modules javafx.controls,javafx.fxml`
    }

}
