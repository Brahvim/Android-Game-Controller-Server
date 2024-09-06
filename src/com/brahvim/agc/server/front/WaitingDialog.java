package com.brahvim.agc.server.front;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

class WaitingDialog {

	static class Result {
	}

	private WaitingDialog() {
		throw new IllegalAccessError();
	}

	static Dialog<WaitingDialog.Result> open() {
		final Dialog<WaitingDialog.Result> dialog = new Dialog<>();
		final DialogPane dialogPane = dialog.getDialogPane();

		WaitingDialog.addButtonsToPane(dialogPane);

		// final Text label = new Text("aaaaaaaaaaaaaaaaaaaaaaa");
		dialog.setHeaderText("aaaaaaaaaaaaa");

		dialog.setTitle("AndroidGameController - Awaiting Clients...");
		dialog.setHeight(240);
		dialog.setWidth(720);
		dialog.setOnCloseRequest(p_event -> dialog.close());
		dialog.setResizable(false);
		dialog.show();

		return dialog;
	}

	private static void addButtonsToPane(final DialogPane p_pane) {
		final ButtonType btnCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		p_pane.getButtonTypes().addAll(

				btnCancel

		);

		// So you feel this is dangerous for business logic on slow computers?:

		p_pane.lookupButton(btnCancel).setOnMouseClicked(p_event -> {

		});

		// ...Well, remember is that the dialog is *not shown yet!*
		// Sure, it gets *even slower* because the implementation
	}

}
