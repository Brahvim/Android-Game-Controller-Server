package com.brahvim.agc.server.front;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.brahvim.agc.server.App;
import com.brahvim.agc.server.back.Backend;
import com.brahvim.agc.server.back.EventAwaitOneClient;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

@SuppressWarnings("unused")
public final class JavaFxApp extends Application {

	// region Fields.
	private static final Screen PRIMARY_SCREEN = Screen.getPrimary();
	public static final Rectangle2D PRIMARY_SCREEN_RECT = JavaFxApp.PRIMARY_SCREEN.getBounds();
	public static final double PRIMARY_SCREEN_WIDTH = JavaFxApp.PRIMARY_SCREEN_RECT.getWidth();
	public static final double PRIMARY_SCREEN_HEIGHT = JavaFxApp.PRIMARY_SCREEN_RECT.getHeight();

	private static final Font fontLarge = new Font(18);
	private static final ArrayList<KeyCode> pressedKeys = new ArrayList<>();
	private static final ArrayList<String> waitingClients = new ArrayList<>();

	// NOSONAR, these *are to be used* **anywhere** in this class!:
	private static HBox row1 = null; // NOSONAR!
	private static Stage stage = null; // NOSONAR!
	private static Scene scene = null; // NOSONAR!
	private static Pane paneRoot = null; // NOSONAR!
	private static Button buttonSeparator = null; // NOSONAR!
	private static Label labelForClientsList = null; // NOSONAR!
	private static Label labelForOptionsList = null; // NOSONAR!
	private static ListView<String> listViewForClients = null; // NOSONAR!
	private static ListView<String> listViewForOptions = null; // NOSONAR!
	// endregion

	@Override
	public void stop() throws Exception {
		Backend.shutdown();
		System.exit(1); // COULD BE a JavaFX crash!
	}

	@Override
	public void start(final Stage p_stage) {
		final var localLabelForClientsList = JavaFxApp.labelForClientsList = new Label("Phones:");
		final var localLabelForOptionsList = JavaFxApp.labelForOptionsList = new Label("Options:");
		final var localListViewForClients = JavaFxApp.listViewForClients = new ListView<>();
		final var localListViewForOptions = JavaFxApp.listViewForOptions = new ListView<>();
		final var localButtonSeparator = JavaFxApp.buttonSeparator = new Button();
		final var localStage = JavaFxApp.stage = p_stage;
		final var localRow2 = JavaFxApp.row1 = new HBox(

				localListViewForClients,
				localButtonSeparator,
				localListViewForOptions

		);

		final var localRow1 = new HBox(localLabelForClientsList, localLabelForOptionsList);
		final var localPaneRoot = JavaFxApp.paneRoot = new VBox(localRow1, localRow2);
		final var localScene = JavaFxApp.scene = new Scene(localPaneRoot);

		this.initStage();
		this.initRootPane();
		this.initClientsList();
		this.initOptionsList();
		this.initSeparatorButton();

		{
			double stageWidthRatio;
			stageWidthRatio = JavaFxApp.stage.getWidth() / 2.8;
			// (Surprisingly, that *was* the correct number to divide by.)

			localListViewForClients.setPrefWidth(stageWidthRatio);
			localLabelForClientsList.setPrefWidth(stageWidthRatio);

			stageWidthRatio = JavaFxApp.stage.getWidth() - stageWidthRatio;
			localListViewForOptions.setPrefWidth(stageWidthRatio);
			localLabelForOptionsList.setPrefWidth(stageWidthRatio);
		}

		localStage.setScene(localScene);
		localStage.show();

		JavaFxApp.stage.setX((JavaFxApp.PRIMARY_SCREEN_WIDTH / 2) - (localStage.getWidth() / 2));
		JavaFxApp.stage.setY((JavaFxApp.PRIMARY_SCREEN_HEIGHT / 2) - (localStage.getHeight() / 2));
	}

	private void initStage() {
		final Stage localStage = JavaFxApp.stage;
		final double width = JavaFxApp.PRIMARY_SCREEN_WIDTH / 4;
		final double height = JavaFxApp.PRIMARY_SCREEN_HEIGHT / 4;

		// final var localDialog = WaitingDialogBuilder.open();

		try (final FileInputStream fis = new FileInputStream("./res/images/icon-192.png")) {
			localStage.getIcons().add(new Image(fis));
		} catch (final IOException e) {
			System.err.println("AGC Icon not found.");
		}

		localStage.setResizable(true);
		// stage.initStyle(StageStyle.TRANSPARENT);
		localStage.setTitle("AndroidGameController - Home");

		localStage.setWidth(width);
		localStage.setHeight(height);

		localStage.setMinWidth(120);
		localStage.setMinHeight(120);

		localStage.setMaxWidth(JavaFxApp.PRIMARY_SCREEN_WIDTH);
		localStage.setMaxHeight(JavaFxApp.PRIMARY_SCREEN_HEIGHT);

		final var localListViewForClients = JavaFxApp.listViewForClients;
		final var localListViewForOptions = JavaFxApp.listViewForOptions;

		final var localLabelForOptionsList = JavaFxApp.labelForOptionsList;
		final var localLabelForClientsList = JavaFxApp.labelForClientsList;

		localStage.widthProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			JavaFxApp.onListViewsWiden(p_newValue.doubleValue(), p_oldValue.doubleValue());
		});

		localStage.heightProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();
			JavaFxApp.onListViewsHeighten(side - (side / 12));
		});
	}

	private void initRootPane() {
		final var localPaneRoot = JavaFxApp.paneRoot;
		localPaneRoot.setStyle("-fx-background-color: black;"); // NOSONAR! Repeated 9 times, but it's CSS!

		// JavaFxApp.paneRoot.getChildren().forEach(c -> {
		// final var cbckKeyPress = c.getOnKeyPressed();
		// });

		localPaneRoot.setOnKeyReleased(p_event -> JavaFxApp.pressedKeys.remove(p_event.getCode()));

		JavaFxApp.prependEventHandlerGivenBothExist(localPaneRoot.onKeyPressedProperty(), p_event -> {
			final KeyCode key = p_event.getCode();

			if (!JavaFxApp.pressedKeys.contains(key))
				JavaFxApp.pressedKeys.add(key);

			final boolean alt = p_event.isAltDown();
			final boolean meta = p_event.isMetaDown();
			final boolean shift = p_event.isShiftDown();
			final boolean ctrl = p_event.isControlDown();

			final boolean onlyCtrl = ctrl && !(alt || meta || shift);
			final boolean onlyShiftCtrl = ctrl && shift && !(alt || meta);

			// We first account for direction based on *actual keys* pressed:

			final int directionFactor = switch (p_event.getCode()) {
				default -> 0; // ...'cause of this guy. CPUs know `* 0` is `0`.
				case LEFT -> -1;
				case RIGHT -> 1;
			};

			if (directionFactor == 0)
				return;

			final double speedFactor =
					/* */ (onlyCtrl // Holding only `Ctrl` => slow and steady (`1px`).
							? 1
							: (onlyShiftCtrl // With `Shift`, it goes a bit faster (`15px`).
									? 15
									: 0)); // If NONE of those keys are active, nothing (`0px`)!

			final double velocity = directionFactor * speedFactor;

			final var localLabelForClients = JavaFxApp.labelForClientsList;
			final var localListViewForClients = JavaFxApp.listViewForClients;

			// final var localLabelForOptions = JavaFxApp.labelForOptionsList;
			// final var localListViewForOptions = JavaFxApp.listViewForOptions;

			final double widthOfClientElements = Math.max(

					JavaFxApp.stage.getWidth() / 4,
					localLabelForClients.getPrefWidth() + velocity

			);

			localLabelForClients.setPrefWidth(widthOfClientElements);
			localListViewForClients.setPrefWidth(widthOfClientElements
			// Math.min(JavaFxApp.stage.getWidth() / 2,
			// localListViewForClients.getWidth() + velocity
			// )
			);

			// localLabelForOptions.setPrefWidth(localLabelForOptions.getPrefWidth() -
			// velocity);
			// localListViewForOptions.setPrefWidth(localListViewForOptions.getWidth() +
			// velocity);
		});
	}

	@SuppressWarnings("unchecked")
	private void initClientsList() {
		final var localListView = JavaFxApp.listViewForClients;
		final var selectionModel = localListView.getSelectionModel();
		final var localLabelForClientsList = JavaFxApp.labelForClientsList;

		localListView.setStyle("-fx-background-color: black;");
		localLabelForClientsList.setStyle("-fx-text-fill: gray;"); // NOSONAR! Can't! It's CSS!

		final AtomicInteger startId = new AtomicInteger(-1); // Also used for drag status - not just the first index!
		final AtomicReference<ListCell<String>> startCell = new AtomicReference<>();

		localListView.setCellFactory(p_listView -> {
			final var toRet = new ListCell<String>() {

				@Override
				protected void updateItem(final String p_label, final boolean p_isEmpty) {
					super.updateItem(p_label, p_isEmpty);

					if (super.isSelected())
						super.setStyle("-fx-background-color: black; -fx-text-fill: white;");
					else
						super.setStyle("-fx-background-color: black; -fx-text-fill: grey;");

					if (p_label != null /* && !p_isEmpty */) {
						super.setCursor(Cursor.CROSSHAIR);
						super.setText(p_label);
					} else { // This CLEARS text when it goes away!
						super.setCursor(Cursor.DEFAULT);
						super.setText(null);
					}
				}

			};

			toRet.setOnMousePressed(p_event -> {
				final int myId = toRet.getIndex();
				final String myText = toRet.getText();
				final var selectedItems = selectionModel.getSelectedItems();

				startId.set(myId);
				startCell.set(toRet);

				// try {

				// FIXME: If newer JavaFX fixes this, use next line (genuine solution):
				selectedItems.remove(myText); // Surprisingly fully reliable!
				// selectionModel.clearAndSelect(myId); // Surprisingly fails every single time.

				// } catch (final Exception e) {
				// Handling this guy causes this solution to fail!
				// }

				toRet.setOnMouseReleased(null);
				System.out.printf("Drag began! Start-ID: `%d`, Text: `%s`.%n", startId.get(), myText);
			});

			// Removed on drag begin, added back upon drag end:
			final EventHandler<MouseEvent> localCbckMouseReleased = p_event -> {
				System.out.printf("Mouse released for `%s`.%n", p_event.getSource());

				final int myId = ((ListCell<String>) p_event.getSource()).getIndex();
				final int localStartId = startId.get();

				if (localStartId == -1) {
					System.out.println("Drag end rejected: Invalid start.");
					return;
				}

				if (localStartId == myId) {
					System.out.println("Drag end rejected: Start and end same.");
					return;
				}

				// Remember: Equality case for this already caused a return:
				final int endId = myId + (localStartId > myId ? -1 : 1);

				startId.set(-1);
				selectionModel.selectRange(localStartId, endId);
				System.out.printf("Drag ended! Start-ID: `%d`, End-ID: `%d`.%n", localStartId, endId);
			};

			toRet.setOnMouseReleased(p_event -> {
				localCbckMouseReleased.handle(p_event);
				startCell.get().setOnMouseReleased(localCbckMouseReleased);
			});

			toRet.setFont(JavaFxApp.fontLarge);

			return toRet;
		});

		localListView.setStyle("-fx-background-color: black;");
		selectionModel.setSelectionMode(SelectionMode.MULTIPLE);

		localListView.setOnKeyPressed(p_event -> {
			final var selectedOption = Option.valueOfLabel(
					JavaFxApp.listViewForOptions.getSelectionModel().getSelectedItem());

			final boolean alt = p_event.isAltDown();
			final boolean meta = p_event.isMetaDown();
			final boolean shift = p_event.isShiftDown();
			final boolean ctrl = p_event.isControlDown();

			final boolean onlyShiftCtrl = ctrl && shift && !(alt || meta);

			// System.out.println("Key pressed with list-view in focus!");
			switch (p_event.getCode()) {

				case INSERT -> JavaFxApp.onSelectionMadeInOptionsList(Option.ADD);

				case DELETE -> {
					if (!onlyShiftCtrl) {
						JavaFxApp.onSelectionMadeInOptionsList(Option.REMOVE);
						return;
					}

					JavaFxApp.onSelectionMadeInOptionsList(Option.STOP);
				}

				case ENTER -> JavaFxApp.onSelectionMadeInOptionsList(Option.CONTROLS);

				default -> {
					//
				}

			}
		});

		localListView.focusedProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			final boolean inFocus = p_newValue; // JavaFX ain't settin' it `null`!...
			localLabelForClientsList.setStyle(inFocus

					? "-fx-text-fill: white;"
					: "-fx-text-fill: gray;"

			);
		});
	}

	private void initOptionsList() {
		final var localListView = JavaFxApp.listViewForOptions;
		final var localLabelForOptionsList = JavaFxApp.labelForOptionsList;

		localListView.requestFocus();
		localListView.setStyle("-fx-background-color: black;");
		localLabelForOptionsList.setStyle("-fx-text-fill: gray;");

		final Option[] options = Option.values();
		final String[] optionLabels = new String[options.length];

		for (int i = 0; i < options.length; ++i)
			optionLabels[i] = options[i].LABEL;

		localListView.setOnKeyPressed(p_event -> {
			final var clientSelections = JavaFxApp.listViewForClients.getSelectionModel();
			final var optionSelections = JavaFxApp.listViewForOptions.getSelectionModel();

			final var selectedItems = clientSelections.getSelectedItems();
			final var selectedOption = Option.valueOfLabel(optionSelections.getSelectedItem());

			switch (p_event.getCode()) {

				case ENTER, SPACE ->
					JavaFxApp.onSelectionMadeInOptionsList(selectedOption);

				default -> {
					return;
				}

			}
		});

		localListView.setCellFactory(p_listView -> {
			final var toRet = new ListCell<String>() {

				@Override
				protected void updateItem(final String p_label, final boolean p_isEmpty) {
					super.updateItem(p_label, p_isEmpty);

					if (super.isFocused() && localListView.isFocused())
						super.setStyle("-fx-background-color: black; -fx-text-fill: white;");
					else
						super.setStyle("-fx-background-color: black; -fx-text-fill: grey;");

					if (p_label != null /* && !p_isEmpty */) {
						super.setOnMouseClicked(p_event -> {
							final var clientSelections = JavaFxApp.listViewForClients.getSelectionModel();
							final var optionSelections = JavaFxApp.listViewForOptions.getSelectionModel();

							final var selectedItems = clientSelections.getSelectedItems();
							final var selectedOption = Option.valueOfLabel(optionSelections.getSelectedItem());

							//
							// if (p_event.getPickResult().getIntersectedNode() != this)
							// return;

							switch (p_event.getButton()) {

								case PRIMARY -> JavaFxApp.onSelectionMadeInOptionsList(selectedOption);

								default -> {
									//
								}

							}
						});

						final String tooltipText = Option.valueOfLabel(p_label).TOOLTIP;

						if (!tooltipText.isEmpty()) {
							final var tooltip = new Tooltip(tooltipText);
							tooltip.setFont(JavaFxApp.fontLarge);
							super.setTooltip(tooltip);
							tooltip.setShowDelay(Duration.seconds(0.15));
						}

						super.setCursor(Cursor.HAND);
						super.setText(p_label);
					} else { // This CLEARS text when it goes away!
						super.setCursor(Cursor.DEFAULT);
						super.setText(null);
					}
				}

			};

			toRet.setFont(JavaFxApp.fontLarge);

			return toRet;
		});

		localListView.focusedProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			final boolean inFocus = p_newValue; // JavaFX ain't settin' it `null`!...
			localLabelForOptionsList.setStyle(inFocus

					? "-fx-text-fill: white;"
					: "-fx-text-fill: gray;"

			);
		});

		localListView.getItems().addAll(optionLabels);
	}

	private void initSeparatorButton() {
		final var localSep = JavaFxApp.buttonSeparator;
		final var localListViewForClients = JavaFxApp.listViewForClients;
		final var localListViewForOptions = JavaFxApp.listViewForOptions;

		localSep.setFocusTraversable(false);
		localSep.setCursor(Cursor.OPEN_HAND);
		localSep.setStyle("-fx-background-color: rgb(50, 50, 50);");
		localSep.setPrefHeight(JavaFxApp.PRIMARY_SCREEN_HEIGHT);

		final AtomicLong lastClickTime = new AtomicLong();
		final AtomicBoolean isDragging = new AtomicBoolean();

		final EventHandler<MouseEvent> localCbckMouseDrag = p_event -> {
			if (!isDragging.get())
				return;

			// System.out.println("DRAGGING!");

			final double dragAmount = p_event.getSceneX() - (localSep.getLayoutX() + (localSep.getWidth() / 2));
			final double widthOfClientElements = localListViewForClients.getWidth() + dragAmount;
			final double widthOfOptionElements = localListViewForOptions.getWidth() - dragAmount;

			final double stageHalf = JavaFxApp.stage.getWidth() / 2;

			localListViewForClients.setPrefWidth(Math.min(
					stageHalf, widthOfClientElements));
			JavaFxApp.labelForClientsList.setPrefWidth(Math.min(
					stageHalf, widthOfClientElements));

			localListViewForOptions.setPrefWidth(Math.max(
					stageHalf, widthOfOptionElements));
			JavaFxApp.labelForOptionsList.setPrefWidth(Math.max(
					stageHalf, widthOfOptionElements));
		};

		localSep.setOnDragDetected(p_eventPressed -> {
			isDragging.set(true);
			localSep.setCursor(Cursor.CLOSED_HAND);

			// System.out.println("Dragging detected!");
			localSep.setOnMouseDragged(localCbckMouseDrag);

			localSep.setOnMouseReleased(p_eventReleased -> {
				isDragging.set(false);
				localSep.setCursor(Cursor.OPEN_HAND);

				// No more useless checks now!:
				localSep.setOnMouseDragged(null);
				localSep.setOnMouseReleased(null);

				// System.out.println("Dragging completed.");
			});
		});
	}

	public static <EventT extends Event> void appendEventHandlerGivenBothExist(

			final ObjectProperty<EventHandler<? super EventT>> p_handlerProperty,
			final EventHandler<? super EventT> p_toAppend

	) {
		if (p_toAppend == null)
			return;

		final EventHandler<? super EventT> registered = p_handlerProperty.get();

		if (registered == null) {
			p_handlerProperty.set(p_toAppend);
			return;
		}

		p_handlerProperty.set(p_event -> {
			registered.handle(p_event);
			p_toAppend.handle(p_event);
		});
	}

	public static <EventT extends Event> void prependEventHandlerGivenBothExist(

			final ObjectProperty<EventHandler<? super EventT>> p_handlerProperty,
			final EventHandler<? super EventT> p_toPrepend

	) {
		if (p_toPrepend == null)
			return;

		final EventHandler<? super EventT> registered = p_handlerProperty.get();

		if (registered == null) {
			p_handlerProperty.set(p_toPrepend);
			return;
		}

		p_handlerProperty.set(p_event -> {
			p_toPrepend.handle(p_event);
			registered.handle(p_event);
		});
	}

	// If I'm not submitting this to an API, `on*()`. Else `cbck*()`.
	private static void onSelectionMadeInOptionsList(final Option p_option) {
		if (p_option == null)
			return;

		final var selections = JavaFxApp.listViewForClients.getSelectionModel().getSelectedItems();

		switch (p_option) {

			case ADD -> {
				final String clientEntry;
				Backend.EDT.publish(EventAwaitOneClient.create());

				clientEntry = App.STRINGS.getFormatted(

						"Client",
						"waiting",
						1 + Backend.INT_CLIENTS_LEFT.getAndIncrement(),
						0

				);

				JavaFxApp.waitingClients.add(clientEntry);
				JavaFxApp.listViewForClients.getItems().add(clientEntry);
				System.out.printf("Added client [%s].%n", clientEntry);
			}

			case STOP -> {
				JavaFxApp.listViewForClients.getItems().removeAll(JavaFxApp.waitingClients);
				Backend.INT_CLIENTS_LEFT.set(0);
				JavaFxApp.waitingClients.clear();

				System.out.println("Now awaiting no clients.");
			}

			case REMOVE -> {
				JavaFxApp.waitingClients.removeAll(selections);
				JavaFxApp.listViewForClients.getItems().removeAll(selections);
				JavaFxApp.listViewForClients.getSelectionModel().clearSelection();
				// optionSelections.clearSelection();

				System.out.printf("Removed clients %s.%n", selections);
			}

			case CONTROLS -> {
				System.out.printf("Controls for clients %s now visible.%n", selections);
			}

		}
	}

	private static void cbckKeyPressedForUndo(final KeyEvent p_event) {
		final boolean alt = p_event.isAltDown();
		final boolean meta = p_event.isMetaDown();
		final boolean shift = p_event.isShiftDown();
		final boolean ctrl = p_event.isControlDown();

		final boolean onlyCtrl = ctrl && !(alt || meta || shift);
		final boolean onlyShiftCtrl = ctrl && shift && !(alt || meta); // If NONE of those keys are active, nothing!
																		// (`0` px!)

		// FIXME: If typing bugs, check *this out!:*

		switch (p_event.getCode()) {

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

	private static void onListViewsHeighten(final double p_listHeight) {
		JavaFxApp.listViewForClients.setPrefHeight(p_listHeight);
		JavaFxApp.listViewForOptions.setPrefHeight(p_listHeight);
	}

	private static void onListViewsWiden(final double p_newValue, final double p_oldValue) {
		final var localListViewForClients = JavaFxApp.listViewForClients;
		final var localListViewForOptions = JavaFxApp.listViewForOptions;
		final var localLabelForClientsList = JavaFxApp.labelForClientsList;
		final var localLabelForOptionsList = JavaFxApp.labelForOptionsList;

		localListViewForClients.setPrefWidth((localListViewForClients.getPrefWidth() / p_oldValue) * p_newValue);
		localListViewForOptions.setPrefWidth((localListViewForOptions.getPrefWidth() / p_oldValue) * p_newValue);
		localLabelForOptionsList.setPrefWidth((localLabelForOptionsList.getPrefWidth() / p_oldValue) * p_newValue);
		localLabelForClientsList.setPrefWidth((localLabelForClientsList.getPrefWidth() / p_oldValue) * p_newValue);
	}

}
