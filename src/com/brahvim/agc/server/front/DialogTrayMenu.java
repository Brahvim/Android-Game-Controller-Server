package com.brahvim.agc.server.front;

import java.awt.event.MouseEvent;

import com.brahvim.agc.server.ExitCode;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class DialogTrayMenu {

	static Dialog<Void> dialog;

	private DialogTrayMenu() {
		throw new IllegalAccessError();
	}

	public static synchronized void close() {
		final var localDialog = DialogTrayMenu.dialog;

		if (localDialog == null)
			return;

		DialogTrayMenu.dialog = null;

		final var paneRoot = localDialog.getDialogPane();
		final var buttonTypeCancel = new ButtonType("Close me!", ButtonData.CANCEL_CLOSE);
		paneRoot.getButtonTypes().addAll(buttonTypeCancel);

		final Button buttonCancel = (Button) paneRoot.lookupButton(buttonTypeCancel);
		// (Have to cast it from `Node` to `Button` anyway!...)

		buttonCancel.setVisible(false);

		final var fadeOut = new FadeTransition(

				Duration.millis(500),
				paneRoot.getScene().getRoot()

		);

		fadeOut.setOnFinished(p_event -> {
			buttonCancel.fire();
		});
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.play();
	}

	public static void onListViewItemSelected(final OptionsTray p_item) {
		switch (p_item) {

			default -> {
				// No defaults!
			}

			case HOME -> StageHome.show();

			case CLOSE -> App.exit(ExitCode.OKAY);

			case PROFILES -> StageProfileChooser.show();

		}
	}

	public static synchronized void show(final MouseEvent p_eventMouseAwt) {
		if (DialogTrayMenu.dialog != null)
			return;

		DialogTrayMenu.dialog = new Dialog<>();

		final var localDialog = DialogTrayMenu.dialog;
		final var paneDialog = localDialog.getDialogPane();
		final var stage = (Stage) paneDialog.getScene().getWindow(); // `Stage` in `HeavyweightDialog`.
		final var listViewItems = FXCollections.observableArrayList(OptionsTray.ORDER_UI);

		final EventHandler<? super KeyEvent> cbckKeyPressExitOnEsc = p_event -> {
			final KeyCode key = p_event.getCode();

			if (key != KeyCode.ESCAPE)
				return;

			if (

			/*		*/ p_event.isAltDown()
					|| p_event.isMetaDown()
					|| p_event.isShiftDown()
					|| p_event.isControlDown()
					|| p_event.isShortcutDown()

			)
				return;

			DialogTrayMenu.close();
		};

		localDialog.initStyle(StageStyle.UNDECORATED);
		localDialog.initModality(Modality.APPLICATION_MODAL);
		localDialog.setTitle(App.getWindowTitle("dialogTray"));

		stage.setWidth(400);
		stage.setHeight(150);
		stage.requestFocus();
		stage.getIcons().add(App.AGC_ICON_IMAGE);
		stage.focusedProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			if (p_newValue.booleanValue())
				return;

			stage.setAlwaysOnTop(false);
			DialogTrayMenu.close();
		});

		paneDialog.getButtonTypes().clear();
		paneDialog.setOnKeyPressed(cbckKeyPressExitOnEsc);
		paneDialog.setStyle("-fx-background-color: black;");
		// paneRoot.setOnMouseExited(p_event -> DialogTrayMenu.close());

		final var listView = new ListView<OptionsTray>(listViewItems);
		final var listViewSelectionModel = listView.getSelectionModel();

		listView.setTranslateY(16.25);
		listView.setPrefWidth(stage.getWidth());
		listView.setPrefHeight(stage.getHeight());

		listView.setCellFactory(p_listView -> {
			final var toRet = new ListCell<OptionsTray>() {

				@Override
				public void updateItem(final OptionsTray p_item, final boolean p_isEmpty) {
					super.updateItem(p_item, p_isEmpty);

					super.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: #808080;");
					super.setFont(App.FONT_LARGE);

					if (p_item != null /* && !p_isEmpty */) {
						super.setOnMouseClicked(p_event -> {

							//
							// if (p_event.getPickResult().getIntersectedNode() != this)
							// return;

							switch (p_event.getButton()) {

								case PRIMARY -> {
									DialogTrayMenu.close();
									DialogTrayMenu.onListViewItemSelected(p_item);
								}

								default -> {
									// No defaults!
								}

							}
						});

						final String tooltipText = p_item.TOOLTIP;

						if (!tooltipText.isEmpty()) {
							final var tooltip = new Tooltip(tooltipText);
							tooltip.setShowDelay(Duration.seconds(0.15));
							tooltip.setFont(App.FONT_LARGE);
							super.setTooltip(tooltip);
						}

						super.setCursor(Cursor.HAND);
						super.setText(p_item.LABEL);
					} else { // This CLEARS text when it goes away!
						super.setCursor(Cursor.DEFAULT);
						super.setText(null);
					}
				}

			};

			toRet.setOnMouseEntered(p_eventEnter -> {
				toRet.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: rgb(255, 255, 255);");

				toRet.setOnMouseExited(p_eventExit -> {
					toRet.setOnMouseExited(null);
					toRet.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: #808080;");
				});

			});

			return toRet;
		});
		listView.setOnKeyPressed(cbckKeyPressExitOnEsc);
		listView.setStyle("-fx-background-color: black;");

		listViewSelectionModel.setSelectionMode(SelectionMode.SINGLE);
		listViewSelectionModel.selectedItemProperty()
				.addListener((p_property, p_oldValue, p_newValue) -> DialogTrayMenu.onListViewItemSelected(p_newValue));

		paneDialog.setContent(listView);

		localDialog.show();
		localDialog.setX(p_eventMouseAwt.getXOnScreen());
		localDialog.setY(p_eventMouseAwt.getYOnScreen());
	}

}
