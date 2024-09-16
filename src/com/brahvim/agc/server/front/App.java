package com.brahvim.agc.server.front;

import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.StringTable;
import com.brahvim.agc.server.back.Backend;
import com.brahvim.agc.server.back.Client;
import com.brahvim.agc.server.back.EventAwaitOneClient;

import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.image.WritableImage;
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
public final class App extends Application {

	// region Fields.
	public static final Font FONT_LARGE = new Font(18);
	public static final Screen PRIMARY_SCREEN = Screen.getPrimary();
	public static final Rectangle2D PRIMARY_SCREEN_RECT = App.PRIMARY_SCREEN.getBounds();
	public static final double PRIMARY_SCREEN_WIDTH = App.PRIMARY_SCREEN_RECT.getWidth();
	public static final double PRIMARY_SCREEN_HEIGHT = App.PRIMARY_SCREEN_RECT.getHeight();
	public static final StringTable STRINGS = StringTable.tryCreating("./res/strings/AgcStringTable.ini");

	public static final Image AGC_ICON_IMAGE = ((Supplier<Image>) () -> {

		try (final FileInputStream fis = new FileInputStream("./res/images/icon-192.png")) {
			return new Image(fis);
		} catch (final IOException e) {
			System.err.println("ICON IMAGE NOT FOUND.");

			final WritableImage toRet = new WritableImage(1, 1);
			toRet.getPixelWriter().setArgb(0, 0, 0x00000000); // Transparent now.

			return toRet;
		}

	}).get();

	static final ArrayList<KeyCode> pressedKeys = new ArrayList<>();
	static final ArrayList<Client> waitingClients = new ArrayList<>();

	// NOSONAR, these *are to be used* **anywhere** in this class!:
	static Stage stage = null; // NOSONAR!
	static Scene scene = null; // NOSONAR!
	static Pane paneRoot = null; // NOSONAR!
	static HBox paneRow1 = null; // NOSONAR!
	static HBox paneRow2 = null; // NOSONAR!
	static Button buttonSeparator = null; // NOSONAR!
	static Label labelClientsList = null; // NOSONAR!
	static Label labelOptionsList = null; // NOSONAR!
	static ListView<Client> listViewClients = null; // NOSONAR!
	static ListView<Option> listViewOptions = null; // NOSONAR!
	// endregion

	// region Static methods.
	public static void main(final String... p_args) {
		// PS Remember to *somehow get these arguments to the JVM* for JavaFX:
		// `--module-path ./lib/openjfx --add-modules javafx.controls,javafx.fxml`
		// (I don't really need the `javafx.fxml` module for this app, but anyway.)

		new Thread(Application::launch, "AGC:FX_APP_LAUNCHER").start();
		EventQueue.invokeLater(AgcTrayIcon::getMenu); // `SwingUtilities.invokeLater()` throws up :/
	}

	public static void exit(final ExitCode p_exitCode) {
		System.out.print(ExitCode.ERROR_MESSAGE_PREFIX);
		System.out.println(p_exitCode.errorMessage);
		System.exit(p_exitCode.ordinal());
	}

	public static void centerStage(final Stage p_stage) {
		p_stage.setX((App.PRIMARY_SCREEN_WIDTH / 2) - (p_stage.getWidth() / 2));
		p_stage.setY((App.PRIMARY_SCREEN_HEIGHT / 2) - (p_stage.getHeight() / 2));
	}

	public static <EventT extends Event> void appendEventHandler(

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

	public static <EventT extends Event> void prependEventHandler(

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

	public static void ensureArrayListSize(final ArrayList<?> p_list, final Integer p_minSize) {
		// `Collection::addAll()`? Well, no!
		// Putting my own `Collection` subclass didn't exactly work out, and `List.of()`
		// won't create a `List` with `null`s! (See its use of `ImmutableCollections`!)
		p_list.ensureCapacity(p_minSize);

		while (p_list.size() <= p_minSize)
			p_list.add(null);
	}

	public static void showStageFocusedAndCentered(final Stage p_stage) {
		Platform.runLater(() -> {
			p_stage.show();
			p_stage.requestFocus();
			App.centerStage(p_stage);
		});
	}

	// If I'm not submitting this to an API, `on*()`. Else `cbck*()`.
	private static void onSelectionMadeInOptionsList(final Option p_option) {
		if (p_option == null)
			return;

		final var items = App.listViewClients.getItems();
		final var selections = App.listViewClients.getSelectionModel().getSelectedItems();

		switch (p_option) {

			case ADD -> {
				Backend.EDT.publish(EventAwaitOneClient.create());
				final Client client = new Client();

				client.setUiEntry(App.STRINGS.getFormatted(

						"ClientsList", "waiting", Backend.INT_CLIENTS_LEFT.incrementAndGet(), 0

				));

				System.out.printf("Added client [%s].%n", client.getUiEntry());
				App.waitingClients.add(client);
				items.add(client);
			}

			case STOP -> {
				Backend.INT_CLIENTS_LEFT.set(0);

				for (final var c : App.waitingClients) {
					items.remove(c);
					c.destroy();
				}

				App.waitingClients.clear();

				System.out.println("Now awaiting no clients.");
			}

			case REMOVE -> {
				App.waitingClients.removeIf(selections::contains);

				for (final var c : selections)
					c.destroy();

				items.removeAll(selections);
				App.listViewClients.getSelectionModel().clearSelection();
				// optionSelections.clearSelection();

				System.out.printf("Removed clients %s.%n", selections);
			}

			case LAYOUT -> {
				System.out.printf("Layout chooser for now visible for client %s.%n", selections);
				StageLayoutChooser.show();
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
		App.listViewClients.setPrefHeight(p_listHeight);
		App.listViewOptions.setPrefHeight(p_listHeight);
	}

	private static void onListViewsWiden(final double p_newValue, final double p_oldValue) {
		final var localListViewClients = App.listViewClients;
		final var localListViewOptions = App.listViewOptions;
		final var localLabelClientsList = App.labelClientsList;
		final var localLabelOptionsList = App.labelOptionsList;

		localListViewClients.setPrefWidth((localListViewClients.getPrefWidth() / p_oldValue) * p_newValue);
		localListViewOptions.setPrefWidth((localListViewOptions.getPrefWidth() / p_oldValue) * p_newValue);
		localLabelOptionsList.setPrefWidth((localLabelOptionsList.getPrefWidth() / p_oldValue) * p_newValue);
		localLabelClientsList.setPrefWidth((localLabelClientsList.getPrefWidth() / p_oldValue) * p_newValue);
	}
	// endregion

	@Override
	public void stop() throws Exception {
		Backend.shutdown();
		// System.exit(1); // COULD BE a JavaFX crash!
	}

	@Override
	public void start(final Stage p_stage) {
		final var localLabelClientsList = App.labelClientsList = new Label("Phones:");
		final var localLabelOptionsList = App.labelOptionsList = new Label("Options:");
		final var localListViewClients = App.listViewClients = new ListView<>();
		final var localListViewOptions = App.listViewOptions = new ListView<>();
		final var localButtonSeparator = App.buttonSeparator = new Button();
		final var localStage = App.stage = p_stage;
		final var localRow2 = App.paneRow2 = new HBox(

				localListViewClients,
				localButtonSeparator,
				localListViewOptions

		);

		final var localRow1 = App.paneRow1 = new HBox(localLabelClientsList, localLabelOptionsList);
		final var localPaneRoot = App.paneRoot = new VBox(localRow1, localRow2);
		final var localScene = App.scene = new Scene(localPaneRoot);

		this.initStage();
		this.initRootPane();
		this.initClientsList();
		this.initOptionsList();
		this.initSeparatorButton();

		{
			double stageWidthRatio;
			stageWidthRatio = App.stage.getWidth() / 2.8;
			// (Surprisingly, that *was* the correct number to divide by.)

			localListViewClients.setPrefWidth(stageWidthRatio);
			localLabelClientsList.setPrefWidth(stageWidthRatio);

			stageWidthRatio = App.stage.getWidth() - stageWidthRatio;

			localLabelOptionsList.setPrefWidth(stageWidthRatio);
			localListViewOptions.setPrefWidth(stageWidthRatio);
		}

		localStage.setScene(localScene);
		localStage.show();

		// StageLayoutChooser.show();

		localStage.setX((App.PRIMARY_SCREEN_WIDTH / 2) - (localStage.getWidth() / 2));
		localStage.setY((App.PRIMARY_SCREEN_HEIGHT / 2) - (localStage.getHeight() / 2));
	}

	private void initStage() {
		final Stage localStage = App.stage;
		final double width = App.PRIMARY_SCREEN_WIDTH / 4;
		final double height = App.PRIMARY_SCREEN_HEIGHT / 4;

		// final var localDialog = WaitingDialogBuilder.open();

		localStage.setResizable(true);
		// stage.initStyle(StageStyle.TRANSPARENT);
		localStage.getIcons().add(App.AGC_ICON_IMAGE);
		localStage.setTitle(App.STRINGS.getString("StageTitles", "home"));

		localStage.setWidth(width);
		localStage.setHeight(height);

		localStage.setMinWidth(120);
		localStage.setMinHeight(120);

		localStage.setMaxWidth(App.PRIMARY_SCREEN_WIDTH);
		localStage.setMaxHeight(App.PRIMARY_SCREEN_HEIGHT);

		final var localListViewClients = App.listViewClients;
		final var localListViewOptions = App.listViewOptions;

		final var localLabelOptionsList = App.labelOptionsList;
		final var localLabelClientsList = App.labelClientsList;

		localStage.widthProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			App.onListViewsWiden(p_newValue.doubleValue(), p_oldValue.doubleValue());
		});

		localStage.heightProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();
			App.onListViewsHeighten(side - (side / 12));
		});
	}

	private void initRootPane() {
		final var localPaneRoot = App.paneRoot;
		localPaneRoot.setStyle("-fx-background-color: rgb(0, 0, 0);"); // NOSONAR! Repeated 9 times, but it's CSS!

		// JavaFxApp.paneRoot.getChildren().forEach(c -> {
		// final var cbckKeyPress = c.getOnKeyPressed();
		// });

		localPaneRoot.setOnKeyReleased(p_event -> App.pressedKeys.remove(p_event.getCode()));

		App.prependEventHandler(localPaneRoot.onKeyPressedProperty(), p_event -> {
			final KeyCode key = p_event.getCode();

			if (!App.pressedKeys.contains(key))
				App.pressedKeys.add(key);

			final boolean alt = p_event.isAltDown();
			final boolean meta = p_event.isMetaDown();
			final boolean shift = p_event.isShiftDown();
			final boolean ctrl = p_event.isControlDown();

			final boolean noMods = !(ctrl || shift || alt || meta);
			final boolean onlyCtrl = ctrl && !(alt || meta || shift);
			final boolean onlyShiftCtrl = ctrl && shift && !(alt || meta);

			// Help menu:
			switch (key) {

				default -> {
					// No defaults!
				}

				case F1 -> {
					if (noMods)
						StageHelp.show();
				}

				case L -> {
					if (!onlyCtrl)
						return;

					StageLayoutChooser.show();
				}

			}

			// List resize!...:
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

			final var localLabelClients = App.labelClientsList;
			final var localListViewClients = App.listViewClients;

			// final var localLabelOptions = JavaFxApp.labelForOptionsList;
			// final var localListViewOptions = JavaFxApp.listViewForOptions;

			final double widthOfClientElements = localLabelClients.getPrefWidth() + velocity;

			localLabelClients.setPrefWidth(widthOfClientElements);
			localListViewClients.setPrefWidth(widthOfClientElements);
		});
	}

	private void initTrayIcon() {

	}

	@SuppressWarnings("unchecked")
	private void initClientsList() {
		final var localListView = App.listViewClients;
		final var localLabelClientsList = App.labelClientsList;

		localListView.setStyle("-fx-background-color: rgb(0, 0, 0);");
		localLabelClientsList.setStyle("-fx-text-fill: gray;"); // NOSONAR! Can't! It's CSS!

		// Used to track dragging:
		final var startId = new AtomicInteger(-1); // Also used for drag **status** - not just the first index!
		final var startCell = new AtomicReference<ListCell<Client>>();

		final var localListViewSelectionModel = localListView.getSelectionModel();

		localListViewSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);

		final var localListViewSelections = localListViewSelectionModel.getSelectedItems();

		localListView.setStyle("-fx-background-color: rgb(0, 0, 0);");
		localListView.setOnKeyPressed(p_event -> {
			final var localListViewOptions = App.listViewOptions;

			final var model = localListViewOptions.getSelectionModel();
			final var option = model.getSelectedItem();

			final boolean alt = p_event.isAltDown();
			final boolean meta = p_event.isMetaDown();
			final boolean shift = p_event.isShiftDown();
			final boolean ctrl = p_event.isControlDown();

			final boolean onlyCtrl = ctrl && !(shift && alt || meta);
			final boolean onlyShiftCtrl = ctrl && shift && !(alt || meta);

			// System.out.println("Key pressed with list-view in focus!");

			// This one matters when the clients list is empty:
			switch (p_event.getCode()) {

				case END, PAGE_DOWN -> {
					if (!localListView.getItems().isEmpty())
						return;

					final var items = localListViewOptions.getItems();
					final var selections = model.getSelectedItems();
					model.clearAndSelect(items.size() - 1);
					localListViewOptions.requestFocus();
				}

				case HOME, PAGE_UP -> {
					if (!localListView.getItems().isEmpty())
						return;

					model.clearAndSelect(0);
					localListViewOptions.requestFocus();
				}

				case DOWN -> {
					if (!localListView.getItems().isEmpty())
						return;

					localListViewOptions.requestFocus();
					model.clearAndSelect(model.getSelectedIndex() + 1);
				}

				case UP -> {
					if (!localListView.getItems().isEmpty())
						return;

					localListViewOptions.requestFocus();
					model.clearAndSelect(model.getSelectedIndex() - 1);
				}

				default -> {
					// No defaults...
				}
			}

			// This one performs actual operations:
			switch (p_event.getCode()) {

				case INSERT -> App.onSelectionMadeInOptionsList(Option.ADD);

				case DELETE -> {
					if (!onlyShiftCtrl) {
						App.onSelectionMadeInOptionsList(Option.REMOVE);
						return;
					}

					App.onSelectionMadeInOptionsList(Option.STOP);
				}

				case ENTER -> App.onSelectionMadeInOptionsList(Option.CONTROLS);

				default -> {
					// No defaults...
				}
			}
		});
		localListView.setCellFactory(p_listView -> {
			final var toRet = new ListCell<Client>() {

				// Default implementation in `ListCell`:

				/*
				 * ```java
				 * protected void updateItem(final T item, final boolean empty) {
				 * ******* this.setItem(item);
				 * ******* this.setEmpty(empty);
				 * ******* if (empty && this.isSelected()) {
				 * ********** this.updateSelected(false);
				 * ******* }
				 * *** }
				 * ```
				 */

				@Override
				protected void updateItem(final Client p_client, final boolean p_isEmpty) {
					super.updateItem(p_client, p_isEmpty);

					if (p_isEmpty)
						localListViewSelectionModel.clearSelection();

					if (super.isSelected())
						super.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: rgb(255, 255, 255);");
					else
						super.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: #808080;");

					// This CLEARS text when it goes away!
					if (p_isEmpty || p_client == null) { // Frequent condition first.
						super.setCursor(Cursor.DEFAULT);
						super.setText(null);
						return;
					}

					final String uiEntry = p_client.getUiEntry();
					super.setText(uiEntry);
					super.setCursor(Cursor.CROSSHAIR);
				}

			};

			toRet.setOnMousePressed(p_event -> {
				final var myId = toRet.getIndex();
				final var myText = toRet.getText();
				final var myClient = toRet.getItem();

				startCell.set(toRet);
				startId.set(myId);

				// try {

				// FIXME: If newer JavaFX fixes this, use next line (genuine solution):
				localListViewSelections.remove(myClient); // Surprisingly fully reliable!
				localListViewSelectionModel.select(myId); // Surprisingly fails every single time.

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
					System.err.println("Drag end rejected: Invalid start.");
					return;
				}

				if (localStartId == myId) {
					System.err.println("Drag end rejected: Start and end same.");
					return;
				}

				// Remember: Equality case for this already caused a return:
				final int endId = myId + (localStartId > myId ? -1 : 1);

				startId.set(-1);
				localListViewSelectionModel.selectRange(localStartId, endId);
				System.out.printf("Drag ended! Start-ID: `%d`, End-ID: `%d`.%n", localStartId, endId);
			};

			toRet.setOnMouseReleased(p_event -> {
				localCbckMouseReleased.handle(p_event);
				startCell.get().setOnMouseReleased(localCbckMouseReleased);
			});

			toRet.setFont(App.FONT_LARGE);

			return toRet;
		});
		localListView.focusedProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			final boolean inFocus = p_newValue; // JavaFX ain't settin' it `null`!...
			localLabelClientsList.setStyle(inFocus

					? "-fx-text-fill: rgb(255, 255, 255);"
					: "-fx-text-fill: gray;"

			);
		});
	}

	private void initOptionsList() {
		final var localListView = App.listViewOptions;
		final var localLabelOptionsList = App.labelOptionsList;

		localListView.requestFocus();
		localListView.getItems().addAll(Option.valuesOrdered());
		localListView.setStyle("-fx-background-color: rgb(0, 0, 0);");

		localLabelOptionsList.setStyle("-fx-text-fill: gray;");

		localListView.setOnKeyPressed(p_event -> {
			final var clientSelections = App.listViewClients.getSelectionModel();
			final var optionSelections = App.listViewOptions.getSelectionModel();

			final var selectedItems = clientSelections.getSelectedItems();
			final var selectedOption = optionSelections.getSelectedItem();

			switch (p_event.getCode()) {

				case ENTER, SPACE ->
					App.onSelectionMadeInOptionsList(selectedOption);

				default -> {
					return;
				}

			}
		});

		localListView.setCellFactory(p_listView -> {
			final var toRet = new ListCell<Option>() {

				@Override
				protected void updateItem(final Option p_option, final boolean p_isEmpty) {
					super.updateItem(p_option, p_isEmpty);

					if (super.isFocused() && localListView.isFocused())
						super.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: rgb(255, 255, 255);");
					else
						super.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: #808080;");

					if (p_option != null /* && !p_isEmpty */) {
						super.setOnMouseClicked(p_event -> {
							final var clientSelections = App.listViewClients.getSelectionModel();
							final var optionSelections = App.listViewOptions.getSelectionModel();

							final var selectedItems = clientSelections.getSelectedItems();
							final var selectedOption = optionSelections.getSelectedItem();

							//
							// if (p_event.getPickResult().getIntersectedNode() != this)
							// return;

							switch (p_event.getButton()) {

								case PRIMARY -> App.onSelectionMadeInOptionsList(selectedOption);

								default -> {
									//
								}

							}
						});

						final String tooltipText = p_option.TOOLTIP;

						if (!tooltipText.isEmpty()) {
							final var tooltip = new Tooltip(tooltipText);
							super.setTooltip(tooltip);
							tooltip.setFont(App.FONT_LARGE);
							tooltip.setShowDelay(Duration.seconds(0.15));
						}

						super.setCursor(Cursor.HAND);
						super.setText(p_option.LABEL);
					} else { // This CLEARS text when it goes away!
						super.setCursor(Cursor.DEFAULT);
						super.setText(null);
					}
				}

			};

			toRet.setFont(App.FONT_LARGE);

			return toRet;
		});

		localListView.focusedProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			final boolean inFocus = p_newValue; // JavaFX ain't settin' it `null`!...
			localLabelOptionsList.setStyle(inFocus

					? "-fx-text-fill: rgb(255, 255, 255);"
					: "-fx-text-fill: gray;"

			);
		});
	}

	private void initSeparatorButton() {
		final var localSep = App.buttonSeparator;
		final var localLabelClients = App.labelClientsList;
		final var localListViewClients = App.listViewClients;

		localSep.setFocusTraversable(false);
		localSep.setCursor(Cursor.OPEN_HAND);
		localSep.setPrefHeight(App.PRIMARY_SCREEN_HEIGHT);
		localSep.setStyle("-fx-background-color: rgb(50, 50, 50);");

		final var lastClickTime = new AtomicLong();
		final var isDragging = new AtomicBoolean();

		final EventHandler<MouseEvent> localCbckMouseDrag = p_event -> {
			if (!isDragging.get())
				return;

			final double mouseX = p_event.getSceneX();

			localLabelClients.setPrefWidth(mouseX);
			localListViewClients.setPrefWidth(mouseX);
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

}
