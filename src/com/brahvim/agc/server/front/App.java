package com.brahvim.agc.server.front;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.StringTable;
import com.brahvim.agc.server.back.Backend;
import com.brahvim.agc.server.back.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {

	// region Fields.
	public static final Font FONT_LARGE = new Font(18);
	private static final Locale LOCALE = Locale.getDefault(); // Order matters!
	public static final Image AGC_ICON_IMAGE = App.loadAgcIcon();
	public static final Screen PRIMARY_SCREEN = Screen.getPrimary();
	public static final Object JAVAFX_APP_THREAD_LOCK = new Object();
	public static final String LANGUAGE = App.LOCALE.getLanguage(); // Order matters!
	public static final StringTable STRINGS = App.findStringTable(); // Order matters!
	public static final ArrayList<Client> LIST_CLIENTS_WAITING = new ArrayList<>(); // NOSONAR!
	public static final boolean ON_MULTI_MONITOR_SETUP = Screen.getScreens().size() > 1;
	public static final Rectangle2D PRIMARY_SCREEN_RECT = App.PRIMARY_SCREEN.getBounds();
	public static final double PRIMARY_SCREEN_WIDTH = App.PRIMARY_SCREEN_RECT.getWidth();
	public static final double PRIMARY_SCREEN_HEIGHT = App.PRIMARY_SCREEN_RECT.getHeight();
	public static final StageSmartResizeFunction SMARTLY_POSITION_STAGE_X = App.ON_MULTI_MONITOR_SETUP
			? StageSmartResizeFunction::implMultiX
			: StageSmartResizeFunction::implSingleX;

	public static final StageSmartResizeFunction SMARTLY_POSITION_STAGE_Y = App.ON_MULTI_MONITOR_SETUP
			? StageSmartResizeFunction::implMultiY
			: StageSmartResizeFunction::implSingleY;
	// endregion

	// region Static methods.
	public static void smartlyPositionSecondOfStages(final Stage p_reference, final Stage p_toPosition) {
		p_toPosition.setX(App.SMARTLY_POSITION_STAGE_X.find(p_reference, p_toPosition));
		p_toPosition.setY(App.SMARTLY_POSITION_STAGE_Y.find(p_reference, p_toPosition));
	}

	public static void ensureArrayListSize(final ArrayList<?> p_list, final Integer p_minSize) {
		// `Collection::addAll()`? Well, no!
		// Putting my own `Collection` subclass didn't exactly work out, and `List.of()`
		// won't create a `List` with `null`s! (See its use of `ImmutableCollections`!)
		p_list.ensureCapacity(p_minSize);

		while (p_list.size() <= p_minSize)
			p_list.add(null);
	}

	// region `getMostCoveredScreen(*)`.
	// The implementation:
	/** @return {@code null} if no {@link Screen} contains it. */
	public static Screen getMostCoveredScreen(final Rectangle2D p_rect) {
		Screen mostCoveredScreen = null;
		double maxAreaRecord = 0;

		for (final Screen screen : Screen.getScreens()) {
			final Rectangle2D
			/*	 */ rectScreen = screen.getVisualBounds(),
					rectInter;

			final double x1 = Math.max(p_rect.getMinX(), rectScreen.getMinX());
			final double y1 = Math.max(p_rect.getMinY(), rectScreen.getMinY());
			final double x2 = Math.min(p_rect.getMaxX(), rectScreen.getMaxX());
			final double y2 = Math.min(p_rect.getMaxY(), rectScreen.getMaxY());

			// if (x2 >= x1 && y2 >= y1) // Let the math *be*.
			rectInter = new Rectangle2D(x1, y1, x2 - x1, y2 - y1);
			// else rectInter = null;
			// Seriously, think about it: What if somebody has multiple monitors that WRAP?

			// if (rectInter == null)
			// return Screen.getPrimary();

			// (Area of intersection):
			final double interArea = rectInter.getWidth() * rectInter.getHeight();

			if (interArea > maxAreaRecord) {
				maxAreaRecord = interArea;
				mostCoveredScreen = screen;
			}
		}

		return mostCoveredScreen;
	}

	/** @return {@code null} if no {@link Screen} contains it. */
	public static Screen getMostCoveredScreen(final Dialog<?> p_dialog) {
		return App.getMostCoveredScreen(new Rectangle2D(

				p_dialog.getX(),
				p_dialog.getY(),
				p_dialog.getWidth(),
				p_dialog.getHeight()

		));
	}

	/** @return {@code null} if no {@link Screen} contains it. */
	public static Screen getMostCoveredScreen(final Rectangle p_rect) {
		return App.getMostCoveredScreen(new Rectangle2D(

				p_rect.getX(),
				p_rect.getY(),
				p_rect.getWidth(),
				p_rect.getHeight()

		));
	}

	/** @return {@code null} if no {@link Screen} contains it. */
	public static Screen getMostCoveredScreen(final Stage p_stage) {
		return App.getMostCoveredScreen(new Rectangle2D(

				p_stage.getX(),
				p_stage.getY(),
				p_stage.getWidth(),
				p_stage.getHeight()

		));
	}

	/** @return {@code null} if no {@link Screen} contains these. */
	public static Screen getMostCoveredScreen(

			final double p_x,
			final double p_y,
			final double p_width,
			final double p_height

	) {
		return App.getMostCoveredScreen(new Rectangle2D(

				p_x,
				p_y,
				p_width,
				p_height

		));
	}
	// endregion

	public static void centerOnPrimaryScreen(final Dialog<?> p_dialog) {
		p_dialog.setX((App.PRIMARY_SCREEN_WIDTH - p_dialog.getWidth()) / 2);
		p_dialog.setY((App.PRIMARY_SCREEN_HEIGHT - p_dialog.getHeight()) / 2);
	}

	public static void centerOnPrimaryScreen(final Stage p_stage) {
		p_stage.setX((App.PRIMARY_SCREEN_WIDTH - p_stage.getWidth()) / 2);
		p_stage.setY((App.PRIMARY_SCREEN_HEIGHT - p_stage.getHeight()) / 2);
	}

	public static <EventT extends Event> void prependEventHandler(

			final ObjectProperty<EventHandler<? super EventT>> p_handlerProperty,
			final EventHandler<? super EventT> p_toPrepend

	) {
		if (p_toPrepend == null)
			return;

		final EventHandler<? super EventT> registered = p_handlerProperty.get();

		if (registered == null) {
			p_handlerProperty.set(p_toPrepend);
			return;
		}

		p_handlerProperty.set(p_event -> {
			p_toPrepend.handle(p_event);
			registered.handle(p_event);
		});
	}

	public static <EventT extends Event> void appendEventHandler(

			final ObjectProperty<EventHandler<? super EventT>> p_handlerProperty,
			final EventHandler<? super EventT> p_toAppend

	) {
		if (p_toAppend == null)
			return;

		final EventHandler<? super EventT> registered = p_handlerProperty.get();

		if (registered == null) {
			p_handlerProperty.set(p_toAppend);
			return;
		}

		p_handlerProperty.set(p_event -> {
			registered.handle(p_event);
			p_toAppend.handle(p_event);
		});
	}

	public static void exit(final ExitCode p_exitCode) {
		System.out.print(ExitCode.ERROR_MESSAGE_PREFIX);
		System.out.println(p_exitCode.errorMessage);
		System.exit(p_exitCode.ordinal());
	}

	public static void main(final String... p_args) {
		Platform.setImplicitExit(false);
		new Thread(Application::launch, "AGC:FX_APP_LAUNCHER").start();
		EventQueue.invokeLater(AgcTrayIcon::getTrayIcon); // `SwingUtilities.invokeLater()` throws up :/
	}

	public static String getWindowTitle(final String p_property) {
		return App.STRINGS.getString("WindowTitles", p_property);
	}

	public static StringTable findStringTable() {
		final var strLang = App.LANGUAGE;
		final var filesStringsDir = new File("./res/strings/").listFiles();
		final var dirsStringsDir = new File[filesStringsDir.length];
		int numDirsFiltered = 0;

		for (final var d : filesStringsDir) {
			if (!d.isDirectory())
				continue;

			dirsStringsDir[numDirsFiltered] = d;
			numDirsFiltered++;
		}

		for (int i = 0; i < dirsStringsDir.length; ++i) {
			final var d = dirsStringsDir[i];

			if (strLang.equals(d.getName())) {
				return StringTable.tryCreating(String.format(

						"./res/strings/%s/agc-string-table.ini",
						strLang

				));
			}
		}

		return StringTable.tryCreating("./res/strings/en/agc-string-table.ini");
	}

	private static Image loadAgcIcon() {
		try (final FileInputStream fis = new FileInputStream("./res/images/agc-icon-192.png")) {
			return new Image(fis);
		} catch (final IOException e) {
			System.err.println("Icon image not found. It will now be grey.");

			final WritableImage toRet = new WritableImage(192, 192);
			final int width = (int) toRet.getWidth(), height = (int) toRet.getHeight();
			final int[] pixels = new int[width * height];

			Arrays.fill(pixels, 0xFF888888); // Grey.

			toRet.getPixelWriter().setPixels(

					0, 0, // From,
					width, height, // To,
					PixelFormat.getIntArgbPreInstance(), // Encoding,
					pixels, // Data,
					0, // **Array** offset,
					width // Stride - size of row. Image must be rectangular, LOL.

			);

			return toRet;
		}
	}
	// endregion

	@Override
	public void start(final Stage p_stage) {
		StageHome.init(p_stage);
	}

	@Override
	public void stop() {
		try {

			final Object lock = App.JAVAFX_APP_THREAD_LOCK;

			while (true) {
				synchronized (lock) {
					lock.wait();
				}
			}

		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		Backend.shutdown();
		System.exit(1); // COULD BE a JavaFX crash!
	}

}
