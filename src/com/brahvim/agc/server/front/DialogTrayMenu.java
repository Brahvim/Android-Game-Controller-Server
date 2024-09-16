package com.brahvim.agc.server.front;

import java.awt.event.MouseEvent;

import javafx.animation.FadeTransition;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class DialogTrayMenu {

	private static final ButtonType BUTTON_DIALOG_CANCEL = new ButtonType("", ButtonData.CANCEL_CLOSE);

	private static Dialog<Void> dialog;

	private DialogTrayMenu() {
		throw new IllegalAccessError();
	}

	public static void show(final MouseEvent p_eventMouseAwt) {
		final var localDialog = DialogTrayMenu.dialog = DialogTrayMenu.init();
		localDialog.show();
		localDialog.setX(p_eventMouseAwt.getXOnScreen());
		localDialog.setY(p_eventMouseAwt.getYOnScreen());

		final var localPaneRoot = localDialog.getDialogPane();
		final var localButtonCancel = localPaneRoot.lookupButton(DialogTrayMenu.BUTTON_DIALOG_CANCEL);
		localButtonCancel.setVisible(false);
	}

	public static void close() {
		final var localDialog = DialogTrayMenu.dialog;
		final var localPaneRoot = DialogTrayMenu.dialog.getDialogPane();
		final Button localButtonCancel = (Button) localPaneRoot.lookupButton(DialogTrayMenu.BUTTON_DIALOG_CANCEL);
		// (Have to cast it from `Node` to `Button` anyway!...)

		final var fadeOut = new FadeTransition(

				Duration.millis(500),
				localDialog.getDialogPane().getScene().getRoot()

		);

		fadeOut.setOnFinished(p_event -> localButtonCancel.fire());
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.play();
	}

	private static Dialog<Void> init() {
		final Dialog<Void> localDialog = new Dialog<>();
		final DialogPane localPaneRoot = localDialog.getDialogPane();

		localDialog.initStyle(StageStyle.TRANSPARENT);
		// localStage.initModality(Modality.WINDOW_MODAL);

		localPaneRoot.setStyle("-fx-background-color: black;");
		localPaneRoot.setOnMouseExited(p_event -> DialogTrayMenu.close());
		localPaneRoot.getButtonTypes().addAll(DialogTrayMenu.BUTTON_DIALOG_CANCEL);

		final var listView = new ListView<OptionTray>();
		final var listViewSelectionModel = listView.getSelectionModel();
		listView.getItems().addAll(OptionTray.valuesOrdered());

		listView.setCellFactory(p_listView -> {
			final var toRet = new ListCell<OptionTray>() {

				@Override
				public void updateItem(final OptionTray p_item, final boolean p_isEmpty) {
					super.updateItem(p_item, p_isEmpty);

					if (super.isFocused() && listView.isFocused())
						super.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: rgb(255, 255, 255);");
					else
						super.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: #808080;");

					if (p_item != null /* && !p_isEmpty */) {
						super.setOnMouseClicked(p_event -> {

							//
							// if (p_event.getPickResult().getIntersectedNode() != this)
							// return;

							switch (p_event.getButton()) {

								case PRIMARY -> DialogTrayMenu.onListViewItemSelected(p_item);

								default -> {
									//
								}

							}
						});

						final String tooltipText = p_item.TOOLTIP;

						if (!tooltipText.isEmpty()) {
							final var tooltip = new Tooltip(tooltipText);
							super.setTooltip(tooltip);
							tooltip.setFont(App.FONT_LARGE);
							tooltip.setShowDelay(Duration.seconds(0.15));
						}

						super.setCursor(Cursor.HAND);
						super.setText(p_item.LABEL);
					} else { // This CLEARS text when it goes away!
						super.setCursor(Cursor.DEFAULT);
						super.setText(null);
					}
				}

			};

			return toRet;
		});

		listViewSelectionModel.setSelectionMode(SelectionMode.SINGLE);

		listViewSelectionModel.selectedItemProperty()
				.addListener((p_property, p_oldValue, p_newValue) -> DialogTrayMenu.onListViewItemSelected(p_newValue));

		localPaneRoot.getChildren().addAll(listView);

		return localDialog;
	}

	private static void onListViewItemSelected(final OptionTray p_item) {
		switch (p_item) {

			case HOME -> {
				
			}

			case CLOSE -> {
			}

			case LAYOUT -> {
			}

			default -> {
			}

		}
	}

}
