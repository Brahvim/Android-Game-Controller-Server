package com.brahvim.agc.server;

import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;
import com.jogamp.newt.opengl.GLWindow;

import processing.awt.PSurfaceAWT;
import processing.core.PApplet;

public class Sketch extends PApplet {

	public JFrame frame;

	@Override
	public void settings() {
		// super.size(100, 100, PConstants.P3D);
	}

	@Override
	public void setup() {
		final Object nativeWindow = super.getSurface().getNative();
		System.out.println(nativeWindow);

		if (nativeWindow instanceof final PSurfaceAWT.SmoothCanvas a) {
			System.out.println("Processing is using Swing.");

			final Frame b = a.getFrame();
			System.out.println(b);

			if (b instanceof final JFrame c) {
				System.out.println("Fetched `JFrame`.");
				System.out.println(c.getDefaultCloseOperation());

				c.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				c.setVisible(false);
				this.frame = c;
			}

		}

		if (nativeWindow instanceof final GLWindow glWindow) {
			System.out.println("Processing is using OpenGL.");
			glWindow.setDefaultCloseOperation(WindowClosingMode.DO_NOTHING_ON_CLOSE);
		}
	}

}
