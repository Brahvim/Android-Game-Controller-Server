package com.brahvim.agc.server.front;

import com.brahvim.agc.server.back.BackendNotification;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JavaFxApp extends Application {

	// region `Application` callbacks.
	@Override
	public void start(final Stage p_stage) throws Exception {
		final var waitingDialog = WaitingDialogBuilder.open(p_stage);
		this.prepareStage(p_stage, this.createScene());
		p_stage.hide();

		BackendNotification.START_BACKEND.fire();
		System.out.println("Frontend now awaiting backend...");

		FrontendNotification.BACKEND_STARTED.onUiThreadWhenFired(() -> { // This will run on the JavaFX thread.
			waitingDialog.close();
			p_stage.show();
		});
	}

	@Override
	public void stop() throws Exception {
	}
	// endregion

	private Scene createScene() {
		final Scene scene = new Scene(this.createPane(

				this.createCloseButton()

		));

		return scene;
	}

	private Button createCloseButton() {
		final Button button = new Button("Press to close.");

		button.setOnAction(event -> {
			System.out.println("Button press detected.");
			Platform.exit();
		});

		return button;
	}

	private Pane createPane(final Node... p_uiElementNodes) {
		final Pane pane = new Pane(p_uiElementNodes);
		return pane;
	}

	private void prepareStage(final Stage p_stage, final Scene p_scene) {
		p_stage.initStyle(StageStyle.TRANSPARENT);
		p_stage.setResizable(true);
		p_stage.setTitle("Test!");
		p_stage.setScene(p_scene);
	}

}
