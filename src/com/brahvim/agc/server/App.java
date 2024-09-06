package com.brahvim.agc.server;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import processing.core.PApplet;

public class App {

	public static void main(final String[] p_args) {
		JFrame.setDefaultLookAndFeelDecorated(false);

		try {
			final var themes = UIManager.getInstalledLookAndFeels();

			// Debian 12 (Bookworm) with Adoptium/Temurin JDK:
			// - GTK+
			// - Metal
			// - Nimbus
			// - CDE/Motif

			for (final var t : themes)
				System.out.println(t);

			UIManager.setLookAndFeel(themes[3].getClassName()); // GTK+ on my computer.

			// System.exit(0);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		final var frame = new JFrame();
		final var listPane = new JPanel();
		final var sliderPane = new JPanel();

		frame.setVisible(false);
		frame.removeNotify();
		frame.setUndecorated(true);
		frame.addNotify();
		frame.setForeground(Color.BLACK);

		// frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(200, 200);
		frame.setVisible(true);

		frame.add(sliderPane);
		frame.add(listPane);

		// listPane.setSize(360, 510);
		listPane.setSize(1000, 100);

		// Initialize sketch separately:
		final Future<Sketch> a = CompletableFuture.supplyAsync(() -> {
			final var sketch = new Sketch();

			PApplet.runSketch(

					new String[] {
							// Processing options go here...
							Sketch.class.getSimpleName(),
					// Sketch options go here.
					},
					sketch

			);

			return sketch;
		});

		frame.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(final ComponentEvent e) {
				final var size = e.getComponent().getSize();
				listPane.setSize(size);
			}

			@Override
			public void componentMoved(final ComponentEvent e) {
				// System.err.println("Unimplemented method 'componentMoved'");
			}

			@Override
			public void componentShown(final ComponentEvent e) {
				// System.err.println("Unimplemented method 'componentShown'");
			}

			@Override
			public void componentHidden(final ComponentEvent e) {
				// System.err.println("Unimplemented method 'componentHidden'");
			}

		});

		final var list = new JList<>(new String[] {
				"Show Sketch!",
				"Bye!",
		});

		listPane.add(list);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		list.addListSelectionListener(e -> {
			if (e.getValueIsAdjusting())
				return;

			final Sketch sketch;
			final int selectedColumn = e.getLastIndex();

			try {
				sketch = a.get();
			} catch (final Exception ex) {
				Thread.currentThread().interrupt();
				return;
			}

			switch (selectedColumn) {
				case 0: {
					sketch.getSurface().setVisible(false);
					sketch.getSurface().setVisible(true);
					break;
				}

				case 1: {
					frame.setVisible(false);
					frame.dispose();
					sketch.exit();
					break;
				}

				default: {
					break;
				}
			}

		});

		final var slider = new JSlider();
		sliderPane.add(slider);
		frame.pack();
	}

}
