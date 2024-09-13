package com.brahvim.agc.server.front;

import java.util.ArrayList;

import com.brahvim.agc.server.back.Backend;
import com.brahvim.agc.server.back.EventAwaitOneClient;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

@SuppressWarnings("unused")
public final class JavaFxApp extends Application {

	// region Fields.
	public static final Rectangle2D PRIMARY_SCREEN_RECT = Screen.getPrimary().getBounds();
	public static final double PRIMARY_SCREEN_WIDTH = JavaFxApp.PRIMARY_SCREEN_RECT.getWidth();
	public static final double PRIMARY_SCREEN_HEIGHT = JavaFxApp.PRIMARY_SCREEN_RECT.getHeight();

	private static final EventHandler<KeyEvent> cbckKeyPressedForUndo = JavaFxApp::cbckKeyPressedForUndo;
	private static final ChangeListener<Number> cbckChangeStageWidthEnsureCenter = JavaFxApp::cbckChangeForStageWidth;

	// NOSONAR, these *are to be used* **anywhere** in this class!:
	private static HBox row1 = null; // NOSONAR!
	private static Stage stage = null; // NOSONAR!
	private static Scene scene = null; // NOSONAR!
	private static Pane paneRoot = null; // NOSONAR!
	private static Label labelForClientsList = null; // NOSONAR!
	private static Label labelForOptionsList = null; // NOSONAR!
	private static ListView<String> listViewForClients = null; // NOSONAR!
	private static ListView<String> listViewForOptions = null; // NOSONAR!
	// endregion

	@Override
	public void stop() throws Exception {
		Backend.shutdown();
	}

	@Override
	public void start(final Stage p_stage) {
		final var localStage = JavaFxApp.stage = p_stage;

		final var localListViewForClients = JavaFxApp.listViewForClients = new ListView<>();
		final var localListViewForOptions = JavaFxApp.listViewForOptions = new ListView<>();

		final var localLabelForClientsList = JavaFxApp.labelForClientsList = new Label("Clients:");
		final var localLabelForOptionsList = JavaFxApp.labelForOptionsList = new Label("Options:");

		final var localRow1 = JavaFxApp.row1 = new HBox(localListViewForClients, localListViewForOptions);
		final var localRow2 = new HBox(localLabelForClientsList, localLabelForOptionsList);
		final var localPaneRoot = JavaFxApp.paneRoot = new VBox(localRow2, localRow1);

		final var localScene = JavaFxApp.scene = new Scene(localPaneRoot);
		localStage.setScene(localScene);

		this.initStage();
		this.initRootPane();
		this.initClientsList();
		this.initOptionsList();

		localStage.show();
		localStage.widthProperty().removeListener(JavaFxApp.cbckChangeStageWidthEnsureCenter);
	}

	private ArrayList<String> createFakeData() {
		final ArrayList<String> toRet = new ArrayList<>();

		for (int i = 1; i <= 5; ++i)
			toRet.add("Client " + i);

		return toRet;
	}

	// region `init*()` methods.
	private void initStage() {
		final double height = JavaFxApp.PRIMARY_SCREEN_HEIGHT / 4;
		final double width = JavaFxApp.PRIMARY_SCREEN_WIDTH / 4;

		// final var dialog = WaitingDialogBuilder.open();
		JavaFxApp.stage.setTitle("AndroidGameController - Home");
		// stage.initStyle(StageStyle.TRANSPARENT);
		JavaFxApp.stage.setResizable(true);

		JavaFxApp.stage.setMinHeight(120);
		JavaFxApp.stage.setMinWidth(120);

		JavaFxApp.stage.setMaxHeight(JavaFxApp.PRIMARY_SCREEN_HEIGHT);
		JavaFxApp.stage.setMaxWidth(JavaFxApp.PRIMARY_SCREEN_WIDTH);

		JavaFxApp.stage.setHeight(height);
		JavaFxApp.stage.setWidth(width);

		JavaFxApp.stage.widthProperty().addListener(JavaFxApp.cbckChangeStageWidthEnsureCenter);

		JavaFxApp.stage.widthProperty().addListener((p_observable, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();
			final double sideHalf = side / 2;

			JavaFxApp.listViewForClients.setPrefWidth(sideHalf);
			JavaFxApp.listViewForOptions.setPrefWidth(sideHalf);

			JavaFxApp.labelForClientsList.setPrefWidth(sideHalf);
			JavaFxApp.labelForOptionsList.setPrefWidth(sideHalf);
		});

		JavaFxApp.stage.heightProperty().addListener((p_observable, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();
			final double listHeight = side - (side / 12);

			JavaFxApp.listViewForClients.setPrefHeight(listHeight);
			JavaFxApp.listViewForOptions.setPrefHeight(listHeight);
		});
	}

	private void initRootPane() {
		JavaFxApp.paneRoot.getChildren().forEach(c -> {
			final var cbckKeyPress = c.getOnKeyPressed();

			c.setOnKeyPressed(cbckKeyPress == null

					? JavaFxApp.cbckKeyPressedForUndo::handle

					: p_keyEvent -> {
						JavaFxApp.cbckKeyPressedForUndo.handle(p_keyEvent);
						cbckKeyPress.handle(p_keyEvent);
					}

			);
		});
	}

	private void initAllLists() {
		JavaFxApp.stage.widthProperty().addListener((p_observable, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();
			JavaFxApp.listViewForClients.setPrefWidth(side / 2);
			JavaFxApp.listViewForOptions.setPrefWidth(side / 2);
		});

		JavaFxApp.stage.heightProperty().addListener((p_observable, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();
			JavaFxApp.listViewForClients.setPrefHeight(side - (side / 12));
			JavaFxApp.listViewForOptions.setPrefHeight(side - (side / 12));
		});
	}

	private void initClientsList() {
		final ListView<String> listView = JavaFxApp.listViewForClients;

		listView.getItems().addAll(this.createFakeData()); // Can't pass to constructor. Weird.
		listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listView.setOnKeyPressed(p_keyEvent -> {
			// System.out.println("Key pressed with list-view in focus!");
			switch (p_keyEvent.getCode()) {

				case DELETE -> listView.getItems().removeAll(listView.getSelectionModel().getSelectedItems());

				default -> {
					//
				}

			}
		});
	}

	private void initOptionsList() {
		final ListView<String> listView = JavaFxApp.listViewForOptions;

		final Option[] options = Option.values();
		final String[] optionLabels = new String[options.length];

		for (int i = 0; i < options.length; ++i)
			optionLabels[i] = options[i].LABEL;

		listView.setOnKeyPressed(p_event -> {
			switch (p_event.getCode()) {

				case ENTER, SPACE ->
					this.cbckSelectionMadeForOptionsList();

				default -> {
					return;
				}

			}

		});

		listView.setCellFactory(p_listView -> new ListCell<String>() {

			@Override
			protected void updateItem(final String p_label, final boolean p_isEmpty) {
				super.updateItem(p_label, p_isEmpty);

				super.setOnMouseClicked(p_event -> {
					//
					// if (p_event.getPickResult().getIntersectedNode() != this)
					// return;

					switch (p_event.getButton()) {

						case PRIMARY -> JavaFxApp.this.cbckSelectionMadeForOptionsList();

						default -> {
							//
						}

					}
				});

				if (p_label != null /* && !p_isEmpty */) {
					final String tooltipText = Option.valueOfLabel(p_label).TOOLTIP;

					if (!tooltipText.isEmpty()) {
						final var tooltip = new Tooltip(tooltipText);
						super.setTooltip(tooltip);
						tooltip.setShowDelay(Duration.seconds(0.15));
					}

					super.setText(p_label);
				} // else { super.setText(null); }
			}

		});

		listView.getItems().addAll(optionLabels);
	}
	// endregion

	private void cbckSelectionMadeForOptionsList() {
		final var clientSelections = JavaFxApp.listViewForClients.getSelectionModel();
		final var optionSelections = JavaFxApp.listViewForOptions.getSelectionModel();

		final var selectedItems = clientSelections.getSelectedItems();
		final var selectedOption = Option.valueOfLabel(optionSelections.getSelectedItem());

		// System.out.printf("Option `%s` will be applied to `%s`.%n", selectedOption,
		// selectedItems);

		if (selectedOption == null)
			return;

		switch (selectedOption) {

			case ADD -> {
				Backend.EDT.publish(EventAwaitOneClient.create());
			}

			case STOP -> {
				System.out.println("Now awaiting no clients.");
				Backend.INT_CLIENTS_LEFT.set(0);
			}

			case REMOVE -> {
				JavaFxApp.listViewForClients.getItems().removeAll(selectedItems);
				JavaFxApp.listViewForClients.getSelectionModel().clearSelection();
			}

			case CONTROLS -> {
				System.out.printf("Controls for clients %s now visible.%n", selectedItems);
			}

		}
	}

	private static void cbckChangeForStageWidth(

			final ObservableValue<? extends Number> p_observable,
			final Number p_oldValue,
			final Number p_newValue

	) {
		JavaFxApp.stage.setX((JavaFxApp.PRIMARY_SCREEN_WIDTH - JavaFxApp.stage.getWidth()) / 2);
		JavaFxApp.stage.setY((JavaFxApp.PRIMARY_SCREEN_HEIGHT - JavaFxApp.stage.getHeight()) / 2);
	}

	private static void cbckKeyPressedForUndo(final KeyEvent p_keyEvent) {
		final boolean alt = p_keyEvent.isAltDown();
		final boolean meta = p_keyEvent.isMetaDown();
		final boolean shift = p_keyEvent.isShiftDown();
		final boolean ctrl = p_keyEvent.isControlDown();

		final boolean onlyCtrl = ctrl && !(alt || meta || shift);

		// TODO: Typing bugs?! Check *this out!:*

		switch (p_keyEvent.getCode()) {

			case Y -> {
				if (!onlyCtrl)
					return;

				System.out.println("`Ctrl` + `Y` seen.");
			}

			case Z -> {
				if (!ctrl)
					return;

				if (onlyCtrl)
					System.out.println("`Ctrl` + `Z` seen.");

				if (shift)
					System.out.println("`Ctrl` + `Shift` + `Z` seen.");
			}

			default -> {
				//
			}

		}
	}

}
