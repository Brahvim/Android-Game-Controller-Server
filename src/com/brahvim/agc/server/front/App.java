package com.brahvim.agc.server.front;

import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

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
	public static final Screen PRIMARY_SCREEN = Screen.getPrimary();
	public static final Object JAVAFX_APP_THREAD_LOCK = new Object();
	public static final Image AGC_ICON_IMAGE = ((Supplier<Image>) () -> {

		try (final FileInputStream fis = new FileInputStream("./res/images/icon-192.png")) {
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

	}).get();
	public static final ArrayList<Client> LIST_CLIENTS_WAITING = new ArrayList<>(); // NOSONAR!
	public static final boolean ON_MULTI_MONITOR_SETUP = Screen.getScreens().size() > 1;
	public static final Rectangle2D PRIMARY_SCREEN_RECT = App.PRIMARY_SCREEN.getBounds();
	public static final double PRIMARY_SCREEN_WIDTH = App.PRIMARY_SCREEN_RECT.getWidth();
	public static final double PRIMARY_SCREEN_HEIGHT = App.PRIMARY_SCREEN_RECT.getHeight();
	public static final StringTable STRINGS = StringTable.tryCreating("./res/strings/AgcStringTable.ini");
	// endregion

	// region Static methods.
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

	@Override
	public void start(final Stage p_stage) {
		// FIXME: Do not forget to remove this!
		StageHome.init(p_stage);
		// StageProfileChooser.show();
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

	public static double findSmartX(

			final Stage p_reference,
			final Stage p_toPosition,
			final Rectangle2D p_rectRefStageScreen

	) {
		// Derived from `p_reference`'s rect:
		final double stageRefX = p_reference.getX();
		// final double stageRefWidth = p_reference.getWidth();

		// `p_toPosition`'s dimensions:
		final double stageMyWidth = p_toPosition.getWidth();

		// Calculation cache:
		// final double stageRefRight = stageRefX + stageRefWidth;

		// final double screenLeft = p_rectRefStageScreen.getMinX();
		// final double screenRight = p_rectRefStageScreen.getMaxX();

		// "Is it" LOL?
		// final boolean isRefOnLeftOrBeyond = stageRefX <= screenLeft;
		// final boolean isRefOnRightOrBeyond = stageRefRight >= screenRight;

		final double screenHalf = p_rectRefStageScreen.getWidth() / 2;

		final double stageMyX = stageRefX +
				(screenHalf > stageRefX
						? +stageMyWidth
						: -stageMyWidth // For `isRefOnRightOrBeyond`.
				);

		// if (isRefOnRightOrBeyond) {
		// stageMyX = stageRefX - stageMyWidth; // Touch right of ref `Stage`, by going
		// to its left!
		// } else if (isRefOnLeftOrBeyond) {
		// stageMyX = stageRefRight; // Touch left of ref `Stage`, by going to its
		// right!
		// }

		// "Project" to screen boundaries if the reference `Stage` is outside it
		// (put against corner rather than edge):

		// if (stageMyX + stageMyWidth > screenRight) {
		// stageMyX = screenRight - stageMyWidth; // Keep to right edge of screen, left
		// of ref.
		// } else if (stageMyX < screenLeft) {
		// stageMyX = screenLeft; // Keep to left edge of screen; right of ref.
		// }

		return stageMyX;
	}

	public static double findSmartY(

			final Stage p_reference,
			final Stage p_toPosition,
			final Rectangle2D p_rectRefStageScreen

	) {
		// Derived from `p_reference`'s rect:
		final double stageRefY = p_reference.getY();
		// final double stageRefHeight = p_reference.getHeight();

		// `p_toPosition`'s dimensions:
		// final double stageMyHeight = p_toPosition.getHeight();

		// // Calculation cache:
		// final double stageRefBottom = stageRefY + stageRefHeight;

		// final double screenTop = p_rectRefStageScreen.getMinY();
		// final double screenBottom = p_rectRefStageScreen.getMaxY();

		// "Is it" LOL?
		// final boolean isRefOnTopOrBeyond = stageRefY <= screenTop;
		// final boolean isRefOnBottomOrBeyond = stageRefBottom >= screenBottom;

		final double stageMyY = stageRefY; // Add/Subtract `stageMyHeight` to have this adjust.
		// ...But you might want to make `x` constant before you *"dynamicize"* this.

		// if (isRefOnBottomOrBeyond) {
		// stageMyY = stageRefY - stageMyHeight; // Touch right of ref `Stage`, by going
		// to its left!
		// } else if (isRefOnTopOrBeyond) {
		// stageMyY = stageRefBottom;
		// }

		// "Project" to screen boundaries if the reference `Stage` is outside it
		// (put against corner rather than edge):

		// if (stageMyY + stageMyHeight > screenBottom) {
		// stageMyY = screenBottom - stageMyHeight; // Be above ref,
		// } else if (stageMyY < screenTop) {
		// stageMyY = screenTop; // Be below ref.
		// }

		return stageMyY;
	}

}
