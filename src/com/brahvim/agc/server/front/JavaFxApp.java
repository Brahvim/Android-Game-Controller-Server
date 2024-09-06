package com.brahvim.agc.server.front;

import com.brahvim.agc.server.back.BackendNotification;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JavaFxApp extends Application {

	// region `Application` callbacks.
	@Override
	public void start(final Stage p_stage) throws Exception {
		final var waitingDialog = WaitingDialog.open();
		this.prepareStage(p_stage, this.createScene());
		p_stage.hide();

		BackendNotification.START_BACKEND.fire();
		System.out.println("Frontend now awaiting backend...");
		new Thread(() -> this.awaitBackendBegin(waitingDialog, p_stage)).start();
	}

	private void awaitBackendBegin(final Dialog<?> p_dialogToClose, final Stage p_stageToShow) {
		FrontendNotification.BACKEND_STARTED.waitForFireAndHandleInterrupts();

		Platform.runLater(() -> { // This will run on the JavaFX thread.
			p_dialogToClose.close();
			p_stageToShow.show();
		});
	}

	@Override
	public void stop() throws Exception {
	}
	// endregion

	private Scene createScene() {
		final Scene scene = new Scene(this.createPane(

				this.createHeyButton()

		));

		return scene;
	}

	private Button createHeyButton() {
		final Button button = new Button("Click to close.");

		button.setOnAction(event -> {
			System.out.println("Click detected.");
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
