package com.brahvim.agc.server.front;

import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

// No virtual calls.
// ...*I hope.*
class WaitingDialogBuilder {

	static class Result {
	}

	private WaitingDialogBuilder() {
		throw new IllegalAccessError();
	}

	public static Dialog<WaitingDialogBuilder.Result> open(final Stage p_stage) {
		final Dialog<WaitingDialogBuilder.Result> dialog = new Dialog<>();
		final DialogPane dialogPane = dialog.getDialogPane();

		WaitingDialogBuilder.addButtonsToPane(dialogPane);

		dialog.setHeaderText("Waiting for client phones to connect...");
		dialog.setTitle("AndroidGameController - Awaiting Clients...");
		dialog.setOnCloseRequest(p_event -> dialog.close());
		dialog.setResizable(false);
		dialog.setHeight(144);
		dialog.setWidth(720);

		// Would've positioned dialog at the center of the screen it *was* on, but...

		// final double stageY = p_stage.getY();
		// final double stageX = p_stage.getX();
		//
		// final Rectangle2D stageRect = new Rectangle2D(
		//
		// stageX,
		// stageY,
		// stageX + p_stage.getWidth(),
		// stageY + p_stage.getHeight()
		//
		// );
		//
		// final var screens = Screen.getScreensForRectangle(stageRect);
		//
		// for (final Screen s : screens) {
		// s.getBounds()
		// }

		// ...But we don't really need that in this scenario, do we?:
		final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

		final double dialogWidth = p_stage.getWidth();
		final double dialogHeight = p_stage.getHeight();

		final double screenWidth = screenBounds.getWidth();
		final double screenHeight = screenBounds.getHeight();

		dialog.setX((screenWidth - dialogWidth) / 2);
		dialog.setY((screenHeight - dialogHeight) / 2);

		dialog.show();

		return dialog;
	}

	private static void addButtonsToPane(final DialogPane p_pane) {
		final ButtonType btnTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		p_pane.getButtonTypes().addAll(

				btnTypeCancel

		);

		final Button btnCancel = (Button) p_pane.lookupButton(btnTypeCancel);
		btnCancel.setOnAction(p_event -> System.exit(0));
	}

	// private static Button getButtonFromDialogPane(final DialogPane p_pane, final
	// ButtonType p_buttonType) {
	// return (Button) p_pane.lookupButton(p_buttonType);
	// }

}
