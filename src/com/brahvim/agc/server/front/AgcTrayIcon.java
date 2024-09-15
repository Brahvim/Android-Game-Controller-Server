package com.brahvim.agc.server.front;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;

import com.brahvim.agc.server.ExitCode;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;

public class AgcTrayIcon {

	public static final Font FONT_LARGE = new Font("Courier", Font.BOLD, 20);

	static final ObservableList<MenuItem> listMenuItems = FXCollections.observableArrayList();

	private static PopupMenu menuInstance;

	private AgcTrayIcon() {
		throw new IllegalAccessError();
	}

	public static void load() {
		AgcTrayIcon.menuInstance = AgcTrayIcon.tryCreating();
	}

	public static PopupMenu getMenuInstance() {
		return AgcTrayIcon.menuInstance;
	}

	public static String getString(final String p_property) {
		return App.STRINGS.getString("TrayList", p_property);
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

		toRet.addActionListener(p_action -> App.showStageFocusedAndCentered(App.stage));

		return toRet;
	}

	public static MenuItem createOptionShowLayoutChooser() {
		final var toRet = new MenuItem(AgcTrayIcon.getString("showLayout"));

		toRet.addActionListener(p_action -> App.showStageFocusedAndCentered(App.stage));

		return toRet;
	}

	private static PopupMenu tryCreating() {
		try {

			return AgcTrayIcon.create();

		} catch (final AWTException e) {
			System.err.println("Couldn't create tray icon.");
			return null;
		}
	}

	private static PopupMenu create() throws AWTException {
		final var menu = new PopupMenu();

		final MenuItem[] menuItems = {

				AgcTrayIcon.createOptionClose(),
				AgcTrayIcon.createOptionShowHome(),
				AgcTrayIcon.createOptionShowLayoutChooser(),

		};

		for (final MenuItem i : menuItems) {
			i.setFont(AgcTrayIcon.FONT_LARGE);
			AgcTrayIcon.listMenuItems.add(i);
			menu.add(i);
		}

		AgcTrayIcon.listMenuItems.addAll(menuItems);
		AgcTrayIcon.listMenuItems.addListener((final Change<? extends MenuItem> p_change) -> {
			final var list = p_change.getList();
			final var max = p_change.getTo();

			for (int i = p_change.getFrom(); i < max; ++i)
				menu.add(list.get(i));
		});

		// menu.setFont(AgcTrayIcon.FONT_LARGE);

		final BufferedImage iconOriginal = SwingFXUtils.fromFXImage(App.AGC_ICON_IMAGE, null);
		final BufferedImage iconResized = new BufferedImage(

				iconOriginal.getWidth(),
				iconOriginal.getHeight(),
				iconOriginal.getType()

		);

		final Graphics2D buffer = iconResized.createGraphics();
		buffer.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		buffer.drawImage(iconOriginal, 0, 0, 24, 24, null);
		buffer.dispose();

		SystemTray.getSystemTray().add(new TrayIcon(

				iconResized,
				"Android Game Controller",
				menu

		));

		return menu;
	}

}
