package com.brahvim.agc.server.front;

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DialogControls {

	private static Dialog<Void> dialog;

	private DialogControls() {
		throw new IllegalAccessError();
	}

	public static void show() {
		if (DialogControls.dialog != null)
			return;

		final var localDialog = DialogControls.dialog = DialogControls.init();
		final DialogPane paneDialog = localDialog.getDialogPane();

		localDialog.show();
		paneDialog.getScene().getWindow().requestFocus();
		paneDialog.requestFocus();

		Window parent = null;

		for (final var s : Window.getWindows()) {
			if (s.isFocused()) {
				parent = s;
				break;
			}
		}

		if (parent == null)
			App.centerOnPrimaryScreen(localDialog);
		else {
			localDialog.setX(

					parent.getX()
							+ (parent.getWidth() / 2)
							- (localDialog.getWidth() / 2)

			);

			localDialog.setY(

					parent.getY()
							+ (parent.getHeight() / 2)
							- (localDialog.getHeight() / 2)

			);
		}
	}

	private static Dialog<Void> init() {
		final var localDialog = new Dialog<Void>();
		final var paneDialog = localDialog.getDialogPane();
		final var paneRoot = new VBox(DialogControls.createLabels());

		final var stageDialog = ((Stage) paneDialog.getScene().getWindow());
		stageDialog.getIcons().add(App.AGC_ICON_IMAGE);

		localDialog.setResizable(false);
		localDialog.setOnCloseRequest(p_event -> {
			DialogControls.dialog = null;
		});
		// localDialog.initStyle(StageStyle.UNDECORATED);
		localDialog.initModality(Modality.APPLICATION_MODAL);
		localDialog.setTitle(App.STRINGS.getString("WindowTitles", "dialogAbout"));

		paneDialog.setStyle("-fx-background-color: black;");
		paneDialog.setContent(paneRoot);

		paneRoot.setStyle("-fx-background-color: black;");

		final var buttonTypeCancel = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
		paneDialog.getButtonTypes().addAll(buttonTypeCancel);

		final var localButtonCancel = (Button) paneDialog.lookupButton(buttonTypeCancel);
		final String strCssFocusFalse = "-fx-background-color: grey; -fx-text-fill: rgb(255, 255, 255);";
		final String strCssFocusedTrue = "-fx-background-color: darkblue; -fx-text-fill: rgb(255, 255, 255);";

		localButtonCancel.setStyle(strCssFocusFalse);
		localButtonCancel.hoverProperty().addListener((p_observable, p_oldValue, p_newValue) -> {
			if (p_newValue.booleanValue()) {
				localButtonCancel.setStyle(strCssFocusedTrue);
			} else {
				localButtonCancel.setStyle(strCssFocusFalse);
			}
		});
		localButtonCancel.focusedProperty().addListener((p_observable, p_oldValue, p_newValue) -> {
			if (p_newValue.booleanValue()) {
				localButtonCancel.setStyle(strCssFocusedTrue);
			} else {
				localButtonCancel.setStyle(strCssFocusFalse);
			}
		});

		return localDialog;
	}

	private static Label[] createLabels() {
		final Label[] toRet = {

				new Label(DialogControls.getString("label0")) //
				, new Label() //

		};

		for (final var l : toRet) {
			l.setFont(App.FONT_LARGE);
			l.setStyle("-fx-text-fill: rgb(255, 255, 255);");
		}

		{ // region `toRet[1]`.
			final var label = toRet[1];
			label.setCursor(Cursor.HAND);
			label.setOnMouseClicked(p_event -> DialogControls.showUrl("urlGitHub"));

			final var text = new Text(DialogControls.getString("label1"));
			text.setFill(Color.web("#007BFF"));
			text.setFont(App.FONT_LARGE);
			text.setUnderline(true);

			label.setGraphic(new TextFlow(text));
		} // endregion

		return toRet;
	}

	private static String getString(final String p_property) {
		return App.STRINGS.getString("DialogControls", p_property);
	}

	private static void showUrl(final String p_url) {
		App.showUrl(DialogControls.getString(p_url));
	}

}
