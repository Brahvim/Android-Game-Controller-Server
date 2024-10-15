package com.brahvim.agc.server.front;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.back.Backend;
import com.brahvim.agc.server.back.Client;
import com.brahvim.agc.server.back.EventAwaitOneClient;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class StageHome {

	// TODO: Number keys as shortcuts? SCROLLING to go through `ListView`s?!

	// region Fields.
	// NOSONAR, these *are to be used* **anywhere** in this class!:
	static Stage stage; // NOSONAR!
	static Scene scene; // NOSONAR!
	static Pane paneRoot; // NOSONAR!
	static HBox paneRow1; // NOSONAR!
	static HBox paneRow2; // NOSONAR!
	static Button buttonSeparator; // NOSONAR!
	static Label labelListClients; // NOSONAR!
	static Label labelListOptions; // NOSONAR!
	static ListView<Client> listViewClients; // NOSONAR!
	static ListView<OptionsHome> listViewOptions; // NOSONAR!
	// endregion

	private StageHome() {
		throw new IllegalAccessError();
	}

	// region Callback methods.
	public static void showStageFocusedAndCentered() {
		Platform.runLater(() -> {
			StageHome.stage.show();
			StageHome.stage.requestFocus();
			StageHome.stage.centerOnScreen();
		});
	}

	/*
	 * private static void cbckKeyPressedForUndo(final KeyEvent p_event) {
	 * final var key = p_event.getCode();
	 * final boolean alt = p_event.isAltDown();
	 * final boolean meta = p_event.isMetaDown();
	 * final boolean shift = p_event.isShiftDown();
	 * final boolean ctrl = p_event.isControlDown();
	 * 
	 * final boolean onlyCtrl = ctrl && !(alt || meta || shift);
	 * final boolean onlyShiftCtrl = ctrl && shift && !(alt || meta); // If NO keys
	 * are active, nothing! (`0` px!)
	 * // FIXME: If typing is buggy, check *this out!:*
	 * 
	 * switch (key) {
	 * 
	 * default -> {
	 * // No defaults!
	 * }
	 * 
	 * case Y -> {
	 * if (!onlyCtrl)
	 * return;
	 * 
	 * System.out.println("`Ctrl` + `Y` seen.");
	 * }
	 * 
	 * case Z -> {
	 * if (!ctrl)
	 * return;
	 * 
	 * if (onlyCtrl)
	 * System.out.println("`Ctrl` + `Z` seen.");
	 * 
	 * if (shift)
	 * System.out.println("`Ctrl` + `Shift` + `Z` seen.");
	 * }
	 * 
	 * }
	 * }
	 */

	// region If I'm not submitting this to an API, `on*()`. Else `cbck*()`.
	private static void onListViewsWiden(final double p_newValue, final double p_oldValue) {
		final var localListViewClients = StageHome.listViewClients;
		final var localListViewOptions = StageHome.listViewOptions;
		final var localLabelClientsList = StageHome.labelListClients;
		final var localLabelOptionsList = StageHome.labelListOptions;

		localListViewClients.setPrefWidth((localListViewClients.getPrefWidth() / p_oldValue) * p_newValue);
		localListViewOptions.setPrefWidth((localListViewOptions.getPrefWidth() / p_oldValue) * p_newValue);
		localLabelOptionsList.setPrefWidth((localLabelOptionsList.getPrefWidth() / p_oldValue) * p_newValue);
		localLabelClientsList.setPrefWidth((localLabelClientsList.getPrefWidth() / p_oldValue) * p_newValue);
	}

	private static void onListViewsHeighten(final double p_listHeight) {
		StageHome.listViewClients.setPrefHeight(p_listHeight);
		StageHome.listViewOptions.setPrefHeight(p_listHeight);
	}

	private static void onOptionSelection(final OptionsHome p_option) {
		if (p_option == null)
			return;

		final var items = StageHome.listViewClients.getItems();
		final var selections = StageHome.listViewClients.getSelectionModel().getSelectedItems();

		switch (p_option) {

			case ADD -> {
				Backend.EDT.publish(EventAwaitOneClient.create());
				final Client client = new Client();

				client.setUiEntry(App.STRINGS.getFormatted(

						"ListHomeClients", "waiting", Backend.INT_CLIENTS_LEFT.incrementAndGet(), 0

				));

				System.out.printf("Added client [%s].%n", client.getUiEntry());
				App.LIST_CLIENTS_WAITING.add(client);
				items.add(client);
			}

			case STOP -> {
				Backend.INT_CLIENTS_LEFT.set(0);

				for (final var c : App.LIST_CLIENTS_WAITING) {
					items.remove(c);
					c.destroy();
				}

				App.LIST_CLIENTS_WAITING.clear();

				System.out.println("Now awaiting no clients.");
			}

			case REMOVE -> {
				App.LIST_CLIENTS_WAITING.removeIf(selections::contains);
				items.removeAll(selections);

				for (final var c : selections)
					c.destroy();

				StageHome.listViewClients.getSelectionModel().clearSelection();

				if (App.LIST_CLIENTS_WAITING.isEmpty())
					Backend.INT_CLIENTS_LEFT.set(0);

				System.out.printf("Removed clients %s.%n", selections);
			}

			case PROFILES -> {
				StageProfiles.show();
			}

			case CONTROLS -> {
				System.out.printf("Controls for clients %s now visible.%n", selections);
			}

		}
	}
	// endregion
	// endregion

	public static synchronized void show() {
		// if (StageHome.stage == null)
		// StageHome.init(new Stage());
		// We start first, and are never `null`!
		final var localStageProfiles = StageProfiles.stage;
		final var localStageHome = StageHome.stage;

		localStageHome.show();
		localStageHome.requestFocus();
		StageHome.listViewOptions.requestFocus();

		if (localStageProfiles == null)
			localStageHome.centerOnScreen();
		else
			App.smartlyPositionSecondOfStages(localStageProfiles, localStageHome);
	}

	public static synchronized void close() {
		StageHome.stage.close();
	}

	public static void init(final Stage p_initStage) {
		final var localLabelListOptions = StageHome.labelListOptions = new Label("Options:");
		final var localLabelListClients = StageHome.labelListClients = new Label("Phones:");
		final var localListViewClients = StageHome.listViewClients = new ListView<>();
		final var localListViewOptions = StageHome.listViewOptions = new ListView<>();
		final var localButtonSeparator = StageHome.buttonSeparator = new Button();
		final var localStage = StageHome.stage = p_initStage;

		final var localRow1 = StageHome.paneRow1 = new HBox(

				localLabelListClients,
				localLabelListOptions

		);

		final var localRow2 = StageHome.paneRow2 = new HBox(

				localListViewClients,
				localButtonSeparator,
				localListViewOptions

		);

		final var localPaneRoot = StageHome.paneRoot = new VBox(

				localRow1,
				localRow2

		);

		final var localScene = StageHome.scene = new Scene(localPaneRoot);

		StageHome.initStage();
		StageHome.initRootPane();
		StageHome.initClientsList();
		StageHome.initOptionsList();
		StageHome.initSeparatorButton();

		double ratioStageWidth;

		ratioStageWidth = StageHome.stage.getWidth() / 2.8;
		// (Surprisingly, that *was* the correct number to divide by.)
		localListViewClients.setPrefWidth(ratioStageWidth);
		localLabelListClients.setPrefWidth(ratioStageWidth);

		ratioStageWidth = StageHome.stage.getWidth() - ratioStageWidth;
		localLabelListOptions.setPrefWidth(ratioStageWidth);
		localListViewOptions.setPrefWidth(ratioStageWidth);

		localStage.setScene(localScene);
		localStage.show();

		App.centerOnPrimaryScreen(localStage);
	}

	private static void initStage() {
		final var localStage = StageHome.stage;
		final var width = App.PRIMARY_SCREEN_WIDTH / 4;
		final var height = App.PRIMARY_SCREEN_HEIGHT / 4;

		localStage.getIcons().add(App.AGC_ICON_IMAGE);
		localStage.setTitle(App.getWindowTitleString("stageHome"));

		localStage.setWidth(width);
		localStage.setHeight(height);

		localStage.setMinWidth(120);
		localStage.setMinHeight(120);

		localStage.setResizable(false);

		localStage.setMaxWidth(App.PRIMARY_SCREEN_WIDTH);
		localStage.setMaxHeight(App.PRIMARY_SCREEN_HEIGHT);

		localStage.widthProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			StageHome.onListViewsWiden(p_newValue.doubleValue(), p_oldValue.doubleValue());
		});

		localStage.heightProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();
			StageHome.onListViewsHeighten(side - (side / 12));
		});
	}

	private static void initRootPane() {
		final var localPaneRoot = StageHome.paneRoot;

		localPaneRoot.setOnKeyPressed(p_event -> {
			final var key = p_event.getCode();
			final boolean alt = p_event.isAltDown();
			final boolean meta = p_event.isMetaDown();
			final boolean shift = p_event.isShiftDown();
			final boolean ctrl = p_event.isControlDown();

			final boolean noMods = !(ctrl || shift || alt || meta);
			final boolean onlyCtrl = ctrl && !(alt || meta || shift);
			final boolean onlyShift = shift && !(ctrl || alt || meta);
			final boolean onlyShiftCtrl = ctrl && shift && !(alt || meta);

			// All operations:
			switch (key) {

				case F -> {
					StageProfiles.show();
				}
				default -> {
					// No defaults!
				}

				case F1 -> {
					if (noMods)
						DialogHelp.show();
				}

				case DELETE -> {
					if (!onlyShiftCtrl)
						return;

					StageHome.onOptionSelection(OptionsHome.STOP);
				}

				case INSERT -> {
					StageHome.onOptionSelection(OptionsHome.ADD);
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
							? 15
							: (onlyShiftCtrl // With `Shift`, it goes a bit faster (`15px`).
									? 35
									: 0)); // If NONE of those keys are active, nothing (`0px`)!

			final double velocity = directionFactor * speedFactor;

			final var localLabelClients = StageHome.labelListClients;
			final var localListViewClients = StageHome.listViewClients;
			final double widthOfClientElements = localLabelClients.getPrefWidth() + velocity;

			localLabelClients.setPrefWidth(widthOfClientElements);
			localListViewClients.setPrefWidth(widthOfClientElements);
		});
		localPaneRoot.setStyle("-fx-background-color: rgb(0, 0, 0);"); // NOSONAR! Repeated 9 times, but it's CSS!
	}

	@SuppressWarnings("unchecked")
	private static void initClientsList() {
		final var localListView = StageHome.listViewClients;
		final var localLabelClientsList = StageHome.labelListClients;

		localListView.setStyle("-fx-background-color: rgb(0, 0, 0);");
		localLabelClientsList.setStyle("-fx-text-fill: gray;"); // NOSONAR! Can't! It's CSS!

		// Used to track dragging:
		final var startId = new AtomicInteger(-1); // Also used for drag **status** - not just the first index!
		final var startCell = new AtomicReference<ListCell<Client>>();

		final var localListViewSelectionModel = localListView.getSelectionModel();
		final var localListViewSelections = localListViewSelectionModel.getSelectedItems();

		localListViewSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);

		localListView.setStyle("-fx-background-color: rgb(0, 0, 0);");
		localListView.setOnKeyPressed(p_event -> {
			final var key = p_event.getCode();

			final boolean alt = p_event.isAltDown();
			final boolean meta = p_event.isMetaDown();
			final boolean shift = p_event.isShiftDown();
			final boolean ctrl = p_event.isControlDown();

			final boolean onlyCtrl = ctrl && !(shift && alt || meta);
			final boolean onlyShift = shift && !(ctrl || alt || meta);
			final boolean onlyShiftCtrl = ctrl && shift && !(alt || meta);

			// System.out.println("Key pressed with list-view in focus!");

			final boolean isListEmpty = localListView.getItems().isEmpty();

			switch (key) {

				case ESCAPE -> {
					StageHome.close();
				}
				default -> {
					// No defaults!
				}
				case Q -> {
					if (onlyCtrl)
						App.exit(ExitCode.OKAY);
				}

				case W -> {
					if (onlyCtrl)
						StageHome.close();
				}

			}

			if (!isListEmpty) {
				switch (key) {

					default -> {
						// No defaults!
					}

					case DELETE -> StageHome.onOptionSelection(OptionsHome.REMOVE);

					case SPACE, ENTER -> StageHome.onOptionSelection(OptionsHome.CONTROLS);

				}

				return;
			}

			final var localListViewOptions = StageHome.listViewOptions;
			final var model = localListViewOptions.getSelectionModel();
			final var option = model.getSelectedItem();

			// This one matters when the clients list is empty:
			switch (key) {

				case END, PAGE_DOWN -> {
					final var items = localListViewOptions.getItems();
					final var selections = model.getSelectedItems();
					model.clearAndSelect(items.size() - 1);
					localListViewOptions.requestFocus();
				}

				case HOME, PAGE_UP -> {
					model.clearAndSelect(0);
					localListViewOptions.requestFocus();
				}

				case DOWN -> {
					localListViewOptions.requestFocus();
					model.clearAndSelect(model.getSelectedIndex() + 1);
				}

				case UP -> {
					localListViewOptions.requestFocus();
					model.clearAndSelect(model.getSelectedIndex() - 1);
				}

				default -> {
					// No defaults!
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

					super.setOnMouseClicked(p_event -> localListView.requestFocus());

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

	private static void initOptionsList() {
		final var localListView = StageHome.listViewOptions;
		final var localLabelOptionsList = StageHome.labelListOptions;

		localLabelOptionsList.setStyle("-fx-text-fill: gray;");

		localListView.requestFocus();
		localListView.setOnKeyPressed(p_event -> {
			final var key = p_event.getCode();

			final boolean alt = p_event.isAltDown();
			final boolean meta = p_event.isMetaDown();
			final boolean shift = p_event.isShiftDown();
			final boolean ctrl = p_event.isControlDown();

			final boolean onlyCtrl = ctrl && !(shift && alt || meta);
			final boolean onlyShift = shift && !(ctrl || alt || meta);
			final boolean onlyShiftCtrl = ctrl && shift && !(alt || meta);

			final var clientSelections = StageHome.listViewClients.getSelectionModel();
			final var optionSelections = StageHome.listViewOptions.getSelectionModel();

			final var selectedItems = clientSelections.getSelectedItems();
			final var selectedOption = optionSelections.getSelectedItem();

			switch (key) {

				case ENTER, SPACE ->
					StageHome.onOptionSelection(selectedOption);

				case ESCAPE -> {
					StageHome.close();
				}

				default -> {
					// No defaults!
				}

				case Q -> {
					if (onlyCtrl)
						App.exit(ExitCode.OKAY);
				}

				case W -> {
					if (onlyCtrl)
						StageHome.close();
				}

			}
		});
		localListView.setCellFactory(p_listView -> {
			final var toRet = new ListCell<OptionsHome>() {

				@Override
				protected void updateItem(final OptionsHome p_option, final boolean p_isEmpty) {
					super.updateItem(p_option, p_isEmpty);

					if (super.isFocused() && localListView.isFocused())
						super.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: rgb(255, 255, 255);");
					else
						super.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: #808080;");

					if (p_option != null /* && !p_isEmpty */) {
						super.setOnMouseClicked(p_event -> {
							final var clientSelections = StageHome.listViewClients.getSelectionModel();
							final var optionSelections = StageHome.listViewOptions.getSelectionModel();

							final var selectedItems = clientSelections.getSelectedItems();
							final var selectedOption = optionSelections.getSelectedItem();

							//
							// if (p_event.getPickResult().getIntersectedNode() != this)
							// return;

							switch (p_event.getButton()) {

								case PRIMARY -> StageHome.onOptionSelection(selectedOption);

								default -> {
									// No defaults!
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
		localListView.getItems().addAll(OptionsHome.ORDER_UI);
		localListView.setStyle("-fx-background-color: rgb(0, 0, 0);");
		localListView.focusedProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			final boolean inFocus = p_newValue; // JavaFX ain't settin' it `null`!...
			localLabelOptionsList.setStyle(inFocus

					? "-fx-text-fill: rgb(255, 255, 255);"
					: "-fx-text-fill: gray;"

			);
		});
	}

	private static void initSeparatorButton() {
		final var localLabelClients = StageHome.labelListClients;
		final var localButtonSeparator = StageHome.buttonSeparator;
		final var localListViewClients = StageHome.listViewClients;

		localButtonSeparator.setFocusTraversable(false);
		localButtonSeparator.setCursor(Cursor.OPEN_HAND);
		localButtonSeparator.setPrefHeight(App.PRIMARY_SCREEN_HEIGHT);
		localButtonSeparator.setStyle("-fx-background-color: rgb(50, 50, 50);");

		final var isDragging = new AtomicBoolean();

		final EventHandler<MouseEvent> localCbckMouseDrag = p_event -> {
			if (!isDragging.get())
				return;

			final double mouseX = p_event.getSceneX();

			localLabelClients.setPrefWidth(mouseX);
			localListViewClients.setPrefWidth(mouseX);
		};

		localButtonSeparator.setOnDragDetected(p_eventPressed -> {
			isDragging.set(true);
			localButtonSeparator.setCursor(Cursor.CLOSED_HAND);

			// System.out.println("Dragging detected!");
			localButtonSeparator.setOnMouseDragged(localCbckMouseDrag);

			localButtonSeparator.setOnMouseReleased(p_eventReleased -> {
				isDragging.set(false);
				localButtonSeparator.setCursor(Cursor.OPEN_HAND);

				// No more useless checks now!:
				localButtonSeparator.setOnMouseDragged(null);
				localButtonSeparator.setOnMouseReleased(null);

				// System.out.println("Dragging completed.");
			});
		});
	}
}
