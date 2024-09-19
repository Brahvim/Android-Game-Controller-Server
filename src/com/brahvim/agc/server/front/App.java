package com.brahvim.agc.server.front;

import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
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
			System.err.println("ICON IMAGE NOT FOUND.");

			final WritableImage toRet = new WritableImage(1, 1);
			toRet.getPixelWriter().setArgb(0, 0, 0x00000000); // Transparent now.

			return toRet;
		}

	}).get();
	public static final ArrayList<Client> LIST_CLIENTS_WAITING = new ArrayList<>(); // NOSONAR1
	public static final Rectangle2D PRIMARY_SCREEN_RECT = App.PRIMARY_SCREEN.getBounds();
	public static final double PRIMARY_SCREEN_WIDTH = App.PRIMARY_SCREEN_RECT.getWidth();
	public static final double PRIMARY_SCREEN_HEIGHT = App.PRIMARY_SCREEN_RECT.getHeight();
	public static final StringTable STRINGS = StringTable.tryCreating("./res/strings/AgcStringTable.ini");
	// endregion

	// region Static methods.
	public static void main(final String... p_args) {
		Platform.setImplicitExit(false);
		new Thread(Application::launch, "AGC:FX_APP_LAUNCHER").start();
		EventQueue.invokeLater(AgcTrayIcon::getTrayIcon); // `SwingUtilities.invokeLater()` throws up :/
	}

	public static void exit(final ExitCode p_exitCode) {
		System.out.print(ExitCode.ERROR_MESSAGE_PREFIX);
		System.out.println(p_exitCode.errorMessage);
		System.exit(p_exitCode.ordinal());
	}

	public static void centerStage(final Stage p_stage) {
		p_stage.setX((App.PRIMARY_SCREEN_WIDTH / 2) - (p_stage.getWidth() / 2));
		p_stage.setY((App.PRIMARY_SCREEN_HEIGHT / 2) - (p_stage.getHeight() / 2));
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

	public static void ensureArrayListSize(final ArrayList<?> p_list, final Integer p_minSize) {
		// `Collection::addAll()`? Well, no!
		// Putting my own `Collection` subclass didn't exactly work out, and `List.of()`
		// won't create a `List` with `null`s! (See its use of `ImmutableCollections`!)
		p_list.ensureCapacity(p_minSize);

		while (p_list.size() <= p_minSize)
			p_list.add(null);
	}

	@Override
	public void start(final Stage p_stage) throws Exception {
		// FIXME: Do not forget to remove this!
		StageHome.init(p_stage);
		// StageProfileChooser.show();
	}

	@Override
	public void stop() throws Exception {
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
