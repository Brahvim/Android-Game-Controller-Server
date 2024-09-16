package com.brahvim.agc.server.front;

import java.awt.Font;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.brahvim.agc.server.ExitCode;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;

// TODO: Make this summon a JavaFX window instead! For cross-platform style points!

public class AgcTrayIcon {

	public static final Font FONT_LARGE = new Font("Courier", Font.BOLD, 20);

	static final ObservableList<MenuItem> listMenuItems = FXCollections.observableArrayList();

	private static PopupMenu menu;

	private AgcTrayIcon() {
		throw new IllegalAccessError();
	}

	public static PopupMenu getMenu() {
		if (AgcTrayIcon.menu == null)
			AgcTrayIcon.menu = AgcTrayIcon.tryCreating();
		return AgcTrayIcon.menu;
	}

	public static MenuItem createOptionClose() {
		final var toRet = new MenuItem(AgcTrayIcon.getString("close"));

		toRet.addActionListener(p_action -> {
			App.exit(ExitCode.OKAY);
		});

		return toRet;
	}

	public static MenuItem createOptionShowHome() {
		final var toRet = new MenuItem(AgcTrayIcon.getString("showHome"));

		toRet.addActionListener(p_action -> App.showStageFocusedAndCentered());

		return toRet;
	}

	public static MenuItem createOptionShowLayoutChooser() {
		final var toRet = new MenuItem(AgcTrayIcon.getString("showLayout"));

		toRet.addActionListener(p_action -> App.showStageFocusedAndCentered());

		return toRet;
	}

	public static String getString(final String p_property) {
		return App.STRINGS.getString("TrayList", p_property);
	}

	private static PopupMenu create() throws Exception { // NOSONAR. Too many exceptions, sorry!
		final var toRet = new PopupMenu();

		final MenuItem[] menuItems = {

				AgcTrayIcon.createOptionClose(),
				AgcTrayIcon.createOptionShowHome(),
				AgcTrayIcon.createOptionShowLayoutChooser(),

		};

		for (final var i : menuItems) {
			i.setFont(AgcTrayIcon.FONT_LARGE);
			AgcTrayIcon.listMenuItems.add(i);
			toRet.add(i);
		}

		AgcTrayIcon.listMenuItems.addAll(menuItems);
		AgcTrayIcon.listMenuItems.addListener((final Change<? extends MenuItem> p_change) -> {
			final var list = p_change.getList();
			final var max = p_change.getTo();

			for (int i = p_change.getFrom(); i < max; ++i)
				toRet.add(list.get(i));
		});

		// menu.setFont(AgcTrayIcon.FONT_LARGE);

		final TrayIcon trayIcon = new TrayIcon(

				SwingFXUtils.fromFXImage(App.AGC_ICON_IMAGE, null),
				"Android Game Controller"

		);

		trayIcon.setImageAutoSize(true);

		trayIcon.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(final MouseEvent p_event) {
				Platform.runLater(() -> DialogTrayMenu.show(p_event));
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

		SystemTray.getSystemTray().add(trayIcon);

		return toRet;
	}

	private static PopupMenu tryCreating() {
		try {

			return AgcTrayIcon.create();

		} catch (final Exception e) {
			System.err.println("Couldn't create tray icon.");
			return null;
		}
	}

}
