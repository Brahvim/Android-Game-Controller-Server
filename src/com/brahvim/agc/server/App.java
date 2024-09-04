package com.brahvim.agc.server;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import processing.core.PApplet;

public class App {

	public static void main(final String[] p_args) {
		JFrame.setDefaultLookAndFeelDecorated(false);
		final Sketch sketch = new Sketch();

		final var frame = new JFrame();
		final var listPane = new JPanel();
		final var sliderPane = new JPanel();

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(200, 200);
		frame.setVisible(true);

		frame.add(sliderPane);
		frame.add(listPane);

		listPane.setSize(1000, 1000);

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
				"Close it off...",
				"Bye!",
		});

		listPane.add(list);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		list.addListSelectionListener(e -> {
			if (e.getValueIsAdjusting())
				return;

			final int selectedColumn = e.getLastIndex();

			switch (selectedColumn) {
				case 0: {
					sketch.frame.setVisible(true);
					break;
				}

				case 1: {
					sketch.die("");
					break;
				}

				case 2: {
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

		PApplet.runSketch(

				new String[] { Sketch.class.getSimpleName() },
				sketch

		);
	}

}
