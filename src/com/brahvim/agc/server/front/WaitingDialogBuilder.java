package com.brahvim.agc.server.front;

import com.brahvim.agc.server.ExitCode;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

// No virtual calls.
// ...*I hope.*

// ...Convenience?! Well, *at least I won't have extra class inheritance data!*
final class WaitingDialogBuilder {

	private WaitingDialogBuilder() {
		throw new IllegalAccessError();
	}

	public static Dialog<ButtonType> open() {
		final Dialog<ButtonType> dialog = new Dialog<>();
		final DialogPane dialogPane = dialog.getDialogPane();

		WaitingDialogBuilder.addButtonsToPane(dialogPane);

		dialog.setHeaderText("Waiting for client phones to connect...");
		dialog.setTitle("AndroidGameController - Awaiting Clients...");
		dialog.setOnCloseRequest(p_event -> dialog.close());
		dialog.setResizable(false);
		dialog.setHeight(144);
		dialog.setWidth(720);

		// Would've positioned dialog at the center of the screen *it was most on*...
		// ...But we don't really need that in this scenario, do we?:

		// Add a listener to the dialog's width and height properties
		final ChangeListener<? super Number> dimensionalChangesListener //
				= (p_property, p_oldValue, p_newValue) -> WaitingDialogBuilder.sendDialogToCenter(dialog);

		dialog.widthProperty().addListener(dimensionalChangesListener);
		// dialog.heightProperty().addListener(dimensionalChangesListener);

		// Setting the `x`-position before the `y`-position or using this combination of
		// calls disallows the expected behavior:

		// dialog.setWidth(720);
		// dialog.setX((screenWidth - dialogWidth) / 2);
		// dialog.setHeight(144);
		// dialog.setY((screenHeight - dialogHeight) / 2);

		// Platform.runLater(dialog::show);

		// YES YES YES, this is *clearly* a race condition.
		// `WaitingDialogBuilder::sendDialogToCenter()` exists for this reason.
		// Attach that guy to the listeners like I did above.

		return dialog;
	}

	private static void sendDialogToCenter(final Dialog<?> p_dialog) {
		// I was using the `Stage` instance instead. Oops!:
		p_dialog.setX((App.PRIMARY_SCREEN_RECT.getWidth() - p_dialog.getWidth()) / 2);
		p_dialog.setY((App.PRIMARY_SCREEN_RECT.getHeight() - p_dialog.getHeight()) / 2);
	}

	private static void addButtonsToPane(final DialogPane p_pane) {
		final ButtonType btnTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		p_pane.getButtonTypes().addAll(

				btnTypeCancel

		);

		final Button btnCancel = (Button) p_pane.lookupButton(btnTypeCancel);
		btnCancel.setOnAction(p_event -> App.exit(ExitCode.OKAY));
	}

	// private static Button getButtonFromDialogPane(final DialogPane p_pane, final
	// ButtonType p_buttonType) {
	// return (Button) p_pane.lookupButton(p_buttonType);
	// }

}
