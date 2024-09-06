package com.brahvim.agc.server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JavaFxApp extends Application {

	@Override
	public void start(final Stage p_stage) throws Exception {
		final Button button = new Button("Hey!");
		final Pane pane = new Pane(button);
		final Scene scene = new Scene(pane);

		final ButtonType okayButtonType = new ButtonType("Okay...");

		final Dialog<Object> dialog = new Dialog<>();
		dialog.getDialogPane().getButtonTypes().addAll(okayButtonType);
		dialog.setOnCloseRequest(e -> dialog.close());
		dialog.setResizable(false);
		dialog.setTitle("Click me!");
		dialog.showAndWait();
		// dialog.getDialogPane().lookupButton(okayButtonType).setDisable(true);

		button.setOnAction(event -> {
			System.out.println("Clicked!");
			Platform.exit();
		});

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
