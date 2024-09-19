package com.brahvim.agc.server.front;

import javafx.scene.control.Dialog;
import javafx.stage.Window;

public final class DialogHelp {

	static Dialog<Void> dialog;

	private DialogHelp() {
		throw new IllegalAccessError();
	}

	public static void show() {
		Window parent = null;

		for (final var s : Window.getWindows()) {
			if (s.isFocused())
				parent = s;
		}

		DialogHelp.dialog.show();
		if (parent == null) {

		}
	}

	public static void close() {
		DialogHelp.dialog.close();
	}

}
