package com.brahvim.agc.server;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import processing.awt.PSurfaceAWT;
import processing.core.PApplet;

public class Sketch extends PApplet {

	@Override
	public void settings() {
		super.size(800, 450); // `16:9` aspect ratio.
	}

	@Override
	public void setup() {
		this.attemptAgc2StyleDecorationRemoval();
		// this.attemptOldStyleDecorationRemoval();
		// super.frameRate(1000);

		// final Object nativeWindow = super.getSurface().getNative();
		// System.out.println(nativeWindow);

		// if (nativeWindow instanceof final PSurfaceAWT.SmoothCanvas a) {
		// System.out.println("Processing is using Swing.");

		// final Frame b = a.getFrame();
		// System.out.println(b);

		// if (b instanceof final JFrame c) {
		// super.frame = c;
		// System.out.println("Fetched `JFrame`.");
		// System.out.println(c.getDefaultCloseOperation());

		// c.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		// c.setVisible(false);
		// c.removeNotify();

		// c.setLayout(null);
		// c.setUndecorated(true);

		// c.addNotify();
		// c.setVisible(true);

		// c.setBackground(new Color(0, 0, 0, 0.1f));
		// }

		// }
	}

	private void attemptOldStyleDecorationRemoval() {
		// This is the dummy variable from Processing.
		final JFrame ret = (JFrame) ((PSurfaceAWT.SmoothCanvas) super.getSurface().getNative()).getFrame();
		ret.setVisible(false);
		ret.removeNotify();
		ret.setUndecorated(true);
		ret.setLayout(null);

		ret.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent p_event) {
				System.out.println("Window closing...");
				// Sketch.agcExit();
			}
		});

		// #region The `JPanel`:
		final JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(final Graphics p_javaGraphics) {
				if (p_javaGraphics instanceof final Graphics2D twoDee) {
					twoDee.drawImage(Sketch.super.g.image, 0, 0, null);
				}
			}
		};

		// Let the `JFrame` be visible and request for `OS` permissions:
		ret.setContentPane(panel); // This is the dummy variable from
		// // Processing.
		panel.setFocusable(true);
		panel.setFocusTraversalKeysEnabled(false);
		panel.requestFocus();
		panel.requestFocusInWindow();

		// Listeners for handling events :+1::
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent p_mouseEvent) {
				Sketch.super.mousePressed = true;
				Sketch.super.mouseButton = p_mouseEvent.getButton();
				Sketch.super.mousePressed();
			}

			@Override
			public void mouseReleased(final MouseEvent p_mouseEvent) {
				Sketch.super.mousePressed = false;
				Sketch.super.mouseReleased();
			}

			@Override
			public void mouseClicked(final MouseEvent p_mouseEvent) {
				Sketch.super.mouseButton = p_mouseEvent.getButton();
				Sketch.super.mouseClicked();
			}
		});

		// Listeners for `mouseDragged()` and `mouseMoved()`:
		panel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(final MouseEvent p_mouseEvent) {
				Sketch.super.mouseX = MouseInfo.getPointerInfo().getLocation().x -
						ret.getLocation().x;
				Sketch.super.mouseY = MouseInfo.getPointerInfo().getLocation().y -
						ret.getLocation().y;
				Sketch.super.mouseDragged();
			}

			@Override
			public void mouseMoved(final MouseEvent p_mouseEvent) {
				Sketch.super.mouseX = MouseInfo.getPointerInfo().getLocation().x -
						ret.getLocation().x;
				Sketch.super.mouseY = MouseInfo.getPointerInfo().getLocation().y -
						ret.getLocation().y;
				Sketch.super.mouseMoved();
			}
		});

		// For `keyPressed()`, `keyReleased()` and `keyTyped()`:
		panel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(final KeyEvent p_keyEvent) {
				Sketch.super.key = p_keyEvent.getKeyChar();
				Sketch.super.keyCode = p_keyEvent.getKeyCode();
				Sketch.super.keyTyped();
			}

			@Override
			public void keyPressed(final KeyEvent p_keyEvent) {
				Sketch.super.key = p_keyEvent.getKeyChar();
				Sketch.super.keyCode = p_keyEvent.getKeyCode();
				// System.out.println("Heard a keypress!");
				Sketch.super.keyPressed();
			}

			@Override
			public void keyReleased(final KeyEvent p_keyEvent) {
				Sketch.super.key = p_keyEvent.getKeyChar();
				Sketch.super.keyCode = p_keyEvent.getKeyCode();
				Sketch.super.keyReleased();
			}
		});

		// Handle `Alt + F4` closes ourselves!:
		// It is kinda 'stupid' to use another listener for optimization, but the
		// reason why multiple listeners are allowed anyway is to let outer code
		// access events and also give you convenience :P

		// PS Notice how this uses `KeyAdapter` instead for
		panel.addKeyListener(new KeyAdapter() {
			@SuppressWarnings("unused")
			boolean exited;

			@Override
			public void keyPressed(final KeyEvent p_keyEvent) {
				if (KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.ALT_DOWN_MASK) != null
						&& p_keyEvent.getKeyCode() == KeyEvent.VK_F4) {
					// Apparently this wasn't the cause of an error I was trying to rectify.
					// However, it *still is a good practice!*
					if (!Sketch.super.exitCalled()) {
						// if (!super.exited)
						// p_exitTask.run();
						this.exited = true;
						p_keyEvent.consume();
					}
				}
			}
		});

		ret.addNotify();
		ret.setVisible(true);
	}

	private synchronized void attemptAgc2StyleDecorationRemoval() {
		synchronized (super.frame) {
			super.frame = ((PSurfaceAWT.SmoothCanvas) super.getSurface().getNative()).getFrame();

			// boolean canAccessFrame = false;
			// while (!canAccessFrame) {
			// try {
			// super.frame.wait();
			// canAccessFrame = true;
			// } catch (final InterruptedException e) {
			// Thread.currentThread().interrupt();
			// }
			// }

			super.frame.removeNotify();
			super.frame.setUndecorated(true);
			super.frame.setBackground(new Color(0, 0, 0, 0.25f));
			super.frame.setLayout(null);

			final JPanel panel = new JPanel() {

				@Override
				protected void paintComponent(final Graphics graphics) {
					if (graphics instanceof final Graphics2D g2d) {
						g2d.drawImage(Sketch.super.g.image, 0, 0, null);
					}
				}

			};

			((JFrame) super.frame).setContentPane(panel);
			panel.setFocusTraversalKeysEnabled(false);
			panel.requestFocus();
			panel.requestFocusInWindow();

			MouseAdapter mA = new MouseAdapter() {
				@Override
				public void mousePressed(final MouseEvent me) {
					Sketch.super.mousePressed = true;
					Sketch.super.mouseButton = me.getButton();
					Sketch.super.mousePressed();
				}

				@Override
				public void mouseReleased(final MouseEvent me) {
					Sketch.super.mousePressed = false;
					Sketch.super.mouseReleased();
				}
			};

			panel.addMouseListener(mA);

			mA = new MouseAdapter() {
				@Override
				public void mouseDragged(final MouseEvent me) {
					Sketch.super.mouseX = (MouseInfo.getPointerInfo().getLocation()).x
							- (Sketch.super.frame.getLocation()).x;
					Sketch.super.mouseY = (MouseInfo.getPointerInfo().getLocation()).y
							- (Sketch.super.frame.getLocation()).y;
					Sketch.super.mouseDragged();
				}

				@Override
				public void mouseMoved(final MouseEvent me) {
					Sketch.super.mouseX = (MouseInfo.getPointerInfo().getLocation()).x
							- (Sketch.super.frame.getLocation()).x;
					Sketch.super.mouseY = (MouseInfo.getPointerInfo().getLocation()).y
							- (Sketch.super.frame.getLocation()).y;
					Sketch.super.mouseMoved();
				}
			};
			panel.addMouseMotionListener(mA);
		}

		super.frame.addNotify();
		// super.frame.notifyAll();
	}

	@Override
	public void draw() {
		// super.frame.setBackground(new Color(0, 0, 0, 0.1f));
		super.background(0, 0, 0, 50);

		if (super.mousePressed)
			super.circle(super.mouseX, super.mouseY, 50);
	}

	@Override
	public void mousePressed() {
		System.out.println("Sketch.mousePressed()");
	}

}
