package com.brahvim.agc.server.front;

import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;

@FunctionalInterface
public interface StageFunctionSmartResize {

	public double find(Stage reference, Stage consumer);

	// Implementations:

	public static double implMultiX(

			final Stage p_reference,
			final Stage p_toPosition

	) {
		// Derived from `p_reference`:
		final Rectangle2D rectScreen = App.getMostCoveredScreen(p_reference).getVisualBounds();
		final double stageMyWidth = p_toPosition.getWidth();
		final double screenHalf = rectScreen.getWidth() / 2;
		final double stageRefX = p_reference.getX();

		return stageRefX +
				(screenHalf > stageRefX
						? +stageMyWidth
						: -stageMyWidth // For `isRefOnRightOrBeyond`.
				);
	}

	public static double implMultiY(

			final Stage p_reference,
			final Stage p_toPosition // NOSONAR! Obey the interface!... :(

	) {
		return p_reference.getY();
	}

	public static double implSingleX(

			final Stage p_reference,
			final Stage p_toPosition

	) {
		// Derived from `p_reference`:
		final double stageRefX = p_reference.getX();
		final double stageRefWidth = p_reference.getWidth();
		final Rectangle2D rectScreen = App.getMostCoveredScreen(p_reference).getVisualBounds();

		// `p_toPosition`'s dimensions:
		final double stageMyWidth = p_toPosition.getWidth();

		// Calculation cache:
		final double stageRefRight = stageRefX + stageRefWidth;

		final double screenLeft = rectScreen.getMinX();
		final double screenRight = rectScreen.getMaxX();

		// "Is it" LOL?
		final boolean isRefOnLeftOrBeyond = stageRefX <= screenLeft;
		final boolean isRefOnRightOrBeyond = stageRefRight >= screenRight;

		final var rect = App.getMostCoveredScreen(p_reference).getVisualBounds();
		final double screenHalf = rect.getWidth() / 2;

		double stageMyX = stageRefX +
				(screenHalf > stageRefX
						? +stageMyWidth
						: -stageMyWidth // For `isRefOnRightOrBeyond`.
				);

		if (isRefOnRightOrBeyond) {
			stageMyX = stageRefX - stageMyWidth; // Touch right of ref `Stage`, by going to its left!
		} else if (isRefOnLeftOrBeyond) {
			stageMyX = stageRefRight; // Touch left of ref `Stage`, by going to its right!
		}

		// "Project" to screen boundaries if the reference `Stage` is outside it
		// (put against corner rather than edge):

		if (stageMyX + stageMyWidth > screenRight) {
			stageMyX = screenRight - stageMyWidth; // Keep to right edge of screen, left of ref.
		} else if (stageMyX < screenLeft) {
			stageMyX = screenLeft; // Keep to left edge of screen; right of ref.
		}

		return stageMyX;
	}

	public static double implSingleY(

			final Stage p_reference,
			final Stage p_toPosition

	) {
		// Derived from `p_reference`:
		final Rectangle2D rectScreen = App.getMostCoveredScreen(p_reference).getVisualBounds();
		final double stageRefHeight = p_reference.getHeight();
		final double stageRefY = p_reference.getY();

		// `p_toPosition`'s dimensions:
		final double stageMyHeight = p_toPosition.getHeight();

		// // Calculation cache:
		final double stageRefBottom = stageRefY + stageRefHeight;

		final double screenTop = rectScreen.getMinY();
		final double screenBottom = rectScreen.getMaxY();

		// "Is it" LOL?
		final boolean isRefOnTopOrBeyond = stageRefY <= screenTop;
		final boolean isRefOnBottomOrBeyond = stageRefBottom >= screenBottom;

		double stageMyY = stageRefY; // Add/Subtract `stageMyHeight` to have this adjust.
		// ...But you might want to make `x` constant before you *"dynamicize"* this.

		if (isRefOnBottomOrBeyond) {
			stageMyY = stageRefY - stageMyHeight; // Touch right of ref `Stage`, by going to its left!
		} else if (isRefOnTopOrBeyond) {
			stageMyY = stageRefBottom;
		}

		// "Project" to screen boundaries if the reference `Stage` is outside it
		// (put against corner rather than edge):

		if (stageMyY + stageMyHeight > screenBottom) {
			stageMyY = screenBottom - stageMyHeight; // Be above ref,
		} else if (stageMyY < screenTop) {
			stageMyY = screenTop; // Be below ref.
		}

		return stageMyY;
	}

}
