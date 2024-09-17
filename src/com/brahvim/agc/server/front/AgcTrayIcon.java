package com.brahvim.agc.server.front;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.brahvim.agc.server.ExitCode;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;

public class AgcTrayIcon {

	private static TrayIcon swingTrayIcon;

	private AgcTrayIcon() {
		throw new IllegalAccessError();
	}

	public static TrayIcon getTrayIcon() {
		if (AgcTrayIcon.swingTrayIcon == null)
			AgcTrayIcon.swingTrayIcon = AgcTrayIcon.tryCreating();

		return AgcTrayIcon.swingTrayIcon;
	}

	private static TrayIcon tryCreating() {
		try {

			return AgcTrayIcon.create();

		} catch (final Exception e) {
			System.err.println("Couldn't create tray icon.");
			return null;
		}
	}

	private static TrayIcon create() throws Exception { // NOSONAR. Too many exceptions, sorry!
		final var toRet = new TrayIcon(

				SwingFXUtils.fromFXImage(App.AGC_ICON_IMAGE, null),
				"Android Game Controller"

		);

		toRet.setImageAutoSize(true);

		toRet.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent p_event) {
				switch (p_event.getButton()) {
					default -> {
						// No defaults!
					}
					case MouseEvent.BUTTON2 -> {
						App.exit(ExitCode.OKAY);
					}
					case MouseEvent.BUTTON3, MouseEvent.BUTTON1 -> {
						Platform.runLater(() -> DialogTrayMenu.show(p_event));
					}
					// case MouseEvent.BUTTON1 -> {
					// DialogTrayMenu.onListViewItemSelected(OptionsTray.HOME);
					// }
				}
			}

			@Override
			public void mousePressed(final MouseEvent p_event) {
			}

			@Override
			public void mouseReleased(final MouseEvent p_event) {
			}

			@Override
			public void mouseEntered(final MouseEvent p_event) {
			}

			@Override
			public void mouseExited(final MouseEvent p_event) {
			}

		});

		SystemTray.getSystemTray().add(toRet);

		return toRet;
	}

}
