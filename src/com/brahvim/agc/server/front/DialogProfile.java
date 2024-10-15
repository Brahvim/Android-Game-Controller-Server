package com.brahvim.agc.server.front;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DialogProfile {

	private static Dialog<Void> dialog;

	private DialogProfile() {
		throw new IllegalAccessError();
	}

	public static void show() {
		if (DialogProfile.dialog != null)
			return;

		final var localDialog = DialogProfile.dialog = DialogProfile.init();
		final DialogPane paneDialog = localDialog.getDialogPane();

		localDialog.show();
		paneDialog.getScene().getWindow().requestFocus();
		paneDialog.requestFocus();

		final var localStageProfiles = StageProfiles.stage;
		final var localStage = (Stage) paneDialog.getScene().getWindow();
		App.smartlyPositionSecondOfStages(localStageProfiles, localStage);

		// localDialog.setX(
		//
		// localStageProfiles.getX()
		// + (localStageProfiles.getWidth() / 2)
		// - (localDialog.getWidth() / 2)
		//
		// );

		// localDialog.setY(
		//
		// localStageProfiles.getY()
		// + (localStageProfiles.getHeight() / 2)
		// - (localDialog.getHeight() / 2)
		//
		// );
	}

	private static Dialog<Void> init() {
		final var localDialog = new Dialog<Void>();
		final var paneDialog = localDialog.getDialogPane();
		final var paneCol2Row1 = new HBox();
		final var paneRoot = new VBox(paneCol2Row1);

		final var stageDialog = ((Stage) paneDialog.getScene().getWindow());
		stageDialog.getIcons().add(App.AGC_ICON_IMAGE);

		localDialog.setResizable(false);
		localDialog.setOnCloseRequest(p_event -> {
			DialogProfile.dialog = null;
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

}
