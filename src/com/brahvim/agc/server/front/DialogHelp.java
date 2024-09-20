package com.brahvim.agc.server.front;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;

public final class DialogHelp {

	static Dialog<Void> dialog;

	private DialogHelp() {
		throw new IllegalAccessError();
	}

	public static void show() {
		if (DialogHelp.dialog != null)
			return;

		final var localDialog = DialogHelp.dialog = DialogHelp.init();
		localDialog.show();

		Window parent = null;

		for (final var s : Window.getWindows()) {
			if (s.isFocused())
				parent = s;
		}

		localDialog.show();
		if (parent == null)
			App.centerOnPrimaryScreen(localDialog);
		else {
			localDialog.setX((parent.getWidth() - localDialog.getWidth()) / 2);
			localDialog.setY((parent.getHeight() - localDialog.getHeight()) / 2);
		}
	}

	private static Dialog<Void> init() {
		final var localDialog = new Dialog<Void>();
		final var paneDialog = localDialog.getDialogPane();
		final var paneRoot = new VBox(

				new Label("An app by Brahvim Bhaktvatsal.")

		);

		paneRoot.setStyle("-fx-background-color: black; -fx-text-fill: grey;");

		paneRoot.setOnKeyPressed(p_event -> {

			switch (p_event.getCode()) {

				case ESCAPE -> {
					DialogHelp.close();
				}

				default -> {
				}

			}

		});

		paneDialog.setContent(paneRoot);
		localDialog.initModality(Modality.APPLICATION_MODAL);

		return localDialog;
	}

	public static void close() {
		final var localDialog = DialogTrayMenu.dialog;

		if (localDialog == null)
			return;

		DialogTrayMenu.dialog = null;

		final var paneRoot = localDialog.getDialogPane();
		final var buttonTypeCancel = new ButtonType("Close me!", ButtonData.CANCEL_CLOSE);
		paneRoot.getButtonTypes().addAll(buttonTypeCancel);

		final Button buttonCancel = (Button) paneRoot.lookupButton(buttonTypeCancel);
		// (Have to cast it from `Node` to `Button` anyway!...)

		buttonCancel.setVisible(false);
		buttonCancel.fire();
	}

}
