package com.brahvim.agc.server.front;

import javafx.beans.value.ChangeListener;
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
		dialog.show();

		// Would've positioned dialog at the center of the screen *it was most on*...
		// ...But we don't really need that in this scenario, do we?:

		// Add a listener to the dialog's width and height properties
		final ChangeListener<? super Number> dimensionalChangesListener = //
				(observableValue, oldVal, newVal) -> WaitingDialogBuilder.sendDialogToCenter(dialog);

		dialog.widthProperty().addListener(dimensionalChangesListener);
		dialog.heightProperty().addListener(dimensionalChangesListener);

		// Setting the `x`-position before the `y`-position or using this combination of
		// calls disallows the expected behavior:

		// dialog.setWidth(720);
		// dialog.setX((screenWidth - dialogWidth) / 2);
		// dialog.setHeight(144);
		// dialog.setY((screenHeight - dialogHeight) / 2);

		// Platform.runLater(dialog::show);

		// YES YES YES, this is *clearly* a race condition, and
		// `WaitingDialogBuilder::sendDialogToCenter()` exists for this reason.
		// Attach that guy to the listeners like I did above.

		return dialog;
	}

	private static void sendDialogToCenter(final Dialog<?> p_dialog) {
		final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

		// I was using the `Stage` instance instead. Oops!:
		final double dialogWidth = p_dialog.getWidth();
		final double dialogHeight = p_dialog.getHeight();

		final double screenWidth = screenBounds.getWidth();
		final double screenHeight = screenBounds.getHeight();

		p_dialog.setX((screenWidth - dialogWidth) / 2);
		p_dialog.setY((screenHeight - dialogHeight) / 2);
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
