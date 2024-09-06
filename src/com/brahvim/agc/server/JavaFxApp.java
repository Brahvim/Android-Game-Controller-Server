package com.brahvim.agc.server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JavaFxApp extends Application {

	public static void launchApp() {
		Application.launch();
	}

	@Override
	public void start(final Stage p_stage) throws Exception {
		final Button button = new Button("Hey!");
		final Pane pane = new Pane(button);
		final Scene scene = new Scene(pane);

		button.setOnMouseClicked(event -> System.out.println("Clicked!"));

		p_stage.initStyle(StageStyle.TRANSPARENT);
		p_stage.setResizable(true);
		p_stage.setTitle("Test!");
		p_stage.setScene(scene);
		p_stage.show();
	}

	@Override
	public void stop() throws Exception {
	}

}
