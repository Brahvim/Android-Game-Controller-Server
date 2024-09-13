package com.brahvim.agc.server.front;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.brahvim.agc.server.App;
import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.back.Backend;
import com.brahvim.agc.server.back.EventAwaitOneClient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

@SuppressWarnings("unused")
public final class JavaFxApp extends Application {

	// region Fields.
	public static final Rectangle2D PRIMARY_SCREEN_RECT = Screen.getPrimary().getBounds();
	public static final double PRIMARY_SCREEN_WIDTH = JavaFxApp.PRIMARY_SCREEN_RECT.getWidth();
	public static final double PRIMARY_SCREEN_HEIGHT = JavaFxApp.PRIMARY_SCREEN_RECT.getHeight();

	private static final ArrayList<KeyCode> pressedKeys = new ArrayList<>();

	// NOSONAR, these *are to be used* **anywhere** in this class!:
	private static HBox row1 = null; // NOSONAR!
	private static Stage stage = null; // NOSONAR!
	private static Scene scene = null; // NOSONAR!
	private static Pane paneRoot = null; // NOSONAR!
	private static Rectangle rectangleSep = null; // NOSONAR!
	private static Label labelForClientsList = null; // NOSONAR!
	private static Label labelForOptionsList = null; // NOSONAR!
	private static ListView<String> listViewForClients = null; // NOSONAR!
	private static ListView<String> listViewForOptions = null; // NOSONAR!
	// endregion

	@Override
	public void stop() throws Exception {
		Backend.shutdown();
		App.exit(ExitCode.OKAY);
	}

	@Override
	public void start(final Stage p_stage) {
		final var localLabelForClientsList = JavaFxApp.labelForClientsList = new Label("Clients:");
		final var localLabelForOptionsList = JavaFxApp.labelForOptionsList = new Label("Options:");
		final var localListViewForClients = JavaFxApp.listViewForClients = new ListView<>();
		final var localListViewForOptions = JavaFxApp.listViewForOptions = new ListView<>();
		final var localRectangleSep = JavaFxApp.rectangleSep = new Rectangle();
		final var localStage = JavaFxApp.stage = p_stage;
		final var localRow2 = JavaFxApp.row1 = new HBox(

				localListViewForClients,
				localRectangleSep,
				localListViewForOptions

		);

		final var localRow1 = new HBox(localLabelForClientsList, localLabelForOptionsList);
		final var localPaneRoot = JavaFxApp.paneRoot = new VBox(localRow1, localRow2);
		final var localScene = JavaFxApp.scene = new Scene(localPaneRoot);

		this.initStage();
		this.initRootPane();
		this.initClientsList();
		this.initOptionsList();
		this.initRectangleSep();

		localStage.setScene(localScene);
		localStage.show();

		JavaFxApp.stage.setX((JavaFxApp.PRIMARY_SCREEN_WIDTH / 2) - (localStage.getWidth() / 2));
		JavaFxApp.stage.setY((JavaFxApp.PRIMARY_SCREEN_HEIGHT / 2) - (localStage.getHeight() / 2));
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

		JavaFxApp.stage.widthProperty().addListener((p_observable, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();
			final double sideHalf = side / 2;

			JavaFxApp.listViewForClients.setPrefWidth(side);
			JavaFxApp.listViewForOptions.setPrefWidth(side);

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
		// JavaFxApp.paneRoot.getChildren().forEach(c -> {
		// final var cbckKeyPress = c.getOnKeyPressed();
		// });

		paneRoot.setOnKeyPressed(p_event -> {
			final KeyCode key = p_event.getCode();

			if (!pressedKeys.contains(key))
				pressedKeys.add(key);
		});

		paneRoot.setOnKeyReleased(p_event -> pressedKeys.remove(p_event.getCode()));

		final var cbckKeyPress = paneRoot.getOnKeyPressed();
		paneRoot.setOnKeyPressed(cbckKeyPress == null

				? JavaFxApp::cbckKeyPressedForUndo

				: p_keyEvent -> {
					cbckKeyPress.handle(p_keyEvent);
					JavaFxApp.cbckKeyPressedForUndo(p_keyEvent);
				}

		);
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
					JavaFxApp.cbckSelectionMadeForOptionsList();

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

						case PRIMARY -> JavaFxApp.cbckSelectionMadeForOptionsList();

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

	private void initRectangleSep() {
		final var localSep = JavaFxApp.rectangleSep;
		final var localListViewForClients = JavaFxApp.listViewForClients;
		final var localListViewForOptions = JavaFxApp.listViewForOptions;

		localSep.setWidth(20);

		localSep.setOnMouseDragged(p_eventOnDrag -> {
			System.out.println("Drag began...");

			localSep.setOnMouseDragReleased(p_eventOffDrag -> {
				localSep.setOnMouseDragReleased(null);
				System.out.println("Drag ended...");
			});
		});

	}
	// endregion

	public static <EventT extends Event> void appendEventHandler(

			ObjectProperty<EventHandler<EventT>> p_handlerProperty,
			EventHandler<EventT> p_toAppend

	) {
		EventHandler<EventT> registered = p_handlerProperty.get();
		p_handlerProperty.set(p_event -> {
			registered.handle(p_event);
			p_toAppend.handle(p_event);
		});
	}

	public static <EventT extends Event> void prependEventHandler(

			ObjectProperty<EventHandler<EventT>> p_handlerProperty,
			EventHandler<EventT> p_toPrepend

	) {
		EventHandler<EventT> registered = p_handlerProperty.get();
		p_handlerProperty.set(p_event -> {
			p_toPrepend.handle(p_event);
			registered.handle(p_event);
		});
	}

	private static void cbckSelectionMadeForOptionsList() {
		final var clientSelections = JavaFxApp.listViewForClients.getSelectionModel();
		final var optionSelections = JavaFxApp.listViewForOptions.getSelectionModel();

		final var selectedItems = clientSelections.getSelectedItems();
		final var selectedOption = Option.valueOfLabel(optionSelections.getSelectedItem());

		// No guarantee when method exits + reduces "flicker":
		optionSelections.clearSelection();
		// clientSelections.clearSelection(); // Cleared on its own + this call's buggy!

		// System.out.printf("Option `%s` will be applied to `%s`.%n", selectedOption,
		// selectedItems);

		if (selectedOption == null)
			return;

		switch (selectedOption) {

			case ADD -> {
				Backend.EDT.publish(EventAwaitOneClient.create());
				JavaFxApp.listViewForClients.getItems().add(

						App.STRINGS.getFormatted(

								"Client",
								"waiting",
								1 + Backend.INT_CLIENTS_LEFT.getAndIncrement(),
								0

						)

				);
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

	private static void cbckKeyPressedForUndo(final KeyEvent p_keyEvent) {
		final boolean alt = p_keyEvent.isAltDown();
		final boolean meta = p_keyEvent.isMetaDown();
		final boolean shift = p_keyEvent.isShiftDown();
		final boolean ctrl = p_keyEvent.isControlDown();

		final boolean onlyCtrl = ctrl && !(alt || meta || shift);
		final boolean onlyShiftCtrl = ctrl && shift && !(alt || meta);

		final KeyCode[] keys = {
				KeyCode.SHIFT,
		};

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
