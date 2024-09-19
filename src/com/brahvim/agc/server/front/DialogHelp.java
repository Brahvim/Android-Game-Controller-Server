package com.brahvim.agc.server.front;

import javafx.scene.control.Dialog;
import javafx.stage.Window;

public final class DialogHelp {

	static Dialog<Void> dialog;

	private DialogHelp() {
		throw new IllegalAccessError();
	}

	public static void show() {
		final var localDialog = DialogHelp.dialog;
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

	public static void close() {
		DialogHelp.dialog.close();
	}

}
