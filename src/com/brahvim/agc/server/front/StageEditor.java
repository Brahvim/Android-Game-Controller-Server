package com.brahvim.agc.server.front;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.back.Profile;

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

@SuppressWarnings("unused")
public final class StageEditor {

	// region Fields.
	// NOSONAR, these *are to be used* **anywhere** in this class!:
	static Stage stage; // NOSONAR!
	static Scene scene; // NOSONAR!
	static Pane paneRoot; // NOSONAR!
	static HBox paneRow1; // NOSONAR!
	static HBox paneRow2; // NOSONAR!
	static Button buttonSeparator; // NOSONAR!
	static Label labelListOptions; // NOSONAR!
	static Label labelListProfiles; // NOSONAR!
	static ListView<Profile> listViewProfiles; // NOSONAR!
	static ListView<OptionsEditor> listViewOptions; // NOSONAR!
	// endregion

	// Will do this for two types of data:
	// static final ArrayList<Stage> stages = new ArrayList<>();
	// - Data that needs to be shared,
	// - Data that is **optional**.

	private StageEditor() {
		throw new IllegalAccessError();
	}

	public static synchronized void show() {
		if (StageEditor.stage == null)
			StageEditor.init();

		final var localStageEditor = StageEditor.stage;
		final var localStageHome = StageProfiles.stage;

		localStageEditor.show();
		localStageEditor.requestFocus();
		StageEditor.listViewOptions.requestFocus();

		if (localStageHome == null)
			localStageEditor.centerOnScreen();
		else
			App.smartlyPositionSecondOfStages(localStageHome, localStageEditor);
	}

	public static synchronized void close() {
		if (StageEditor.stage == null)
			return;

		StageEditor.stage.hide();
	}

	public static void showStageFocusedAndCentered() {
		Platform.runLater(StageEditor::show);
	}

	// region Callbacks.
	private static void onOptionSelection(final OptionsEditor p_option) {
		if (p_option == null)
			return;

		final var localListView = StageEditor.listViewProfiles;
		final var items = localListView.getItems();
		final var model = localListView.getSelectionModel();
		final var selections = model.getSelectedItems();

		switch (p_option) {

			default -> {
				// No defaults!
			}

			case ADD -> {
			}

			case REMOVE -> {
			}

		}
	}

	private static void onListViewsWiden(final double p_newValue, final double p_oldValue) {
		final var localListViewOptions = StageEditor.listViewOptions;
		final var localListViewProfiles = StageEditor.listViewProfiles;
		final var localLabelListOptions = StageEditor.labelListOptions;
		final var localLabelListClients = StageEditor.labelListProfiles;

		localListViewOptions.setPrefWidth((localListViewOptions.getPrefWidth() / p_oldValue) * p_newValue);
		localListViewProfiles.setPrefWidth((localListViewProfiles.getPrefWidth() / p_oldValue) * p_newValue);
		localLabelListOptions.setPrefWidth((localLabelListOptions.getPrefWidth() / p_oldValue) * p_newValue);
		localLabelListClients.setPrefWidth((localLabelListClients.getPrefWidth() / p_oldValue) * p_newValue);
	}

	private static void onListViewsHeighten(final double p_listHeight) {
		StageEditor.listViewOptions.setPrefHeight(p_listHeight);
		StageEditor.listViewProfiles.setPrefHeight(p_listHeight);
	}
	// endregion

	private static void init() {
		final var localStage = StageEditor.stage = new Stage();

		final var localLabelListProfiles = StageEditor.labelListProfiles = new Label(

				App.STRINGS.getString("ListEditorControls", "label")

		);

		final var localLabelListOptions = StageEditor.labelListOptions = new Label(

				App.STRINGS.getString("ListEditorOptions", "label")

		);

		final var localListViewProfiles = StageEditor.listViewProfiles = new ListView<>();

		final var localListViewOptions = StageEditor.listViewOptions = new ListView<>();

		final var localButtonSeparator = StageEditor.buttonSeparator = new Button();

		final var localRow1 = StageEditor.paneRow1 = new HBox(

				localLabelListProfiles,
				localLabelListOptions

		);

		final var localRow2 = StageEditor.paneRow2 = new HBox(

				localListViewProfiles,
				localButtonSeparator,
				localListViewOptions

		);

		final var localPaneRoot = StageEditor.paneRoot = new VBox(

				localRow1,
				localRow2

		);

		final var localScene = StageEditor.scene = new Scene(localPaneRoot);

		StageEditor.initStage();
		StageEditor.initRootPane();
		StageEditor.initOptionsList();
		StageEditor.initProfilesList();
		StageEditor.initSeparatorButton();
	}

	private static void initStage() {
		final var localStage = StageEditor.stage;

		final double screenWidth = App.PRIMARY_SCREEN_WIDTH;
		final double screenHeight = App.PRIMARY_SCREEN_HEIGHT;

		final double height = screenHeight / 4;
		final double width = screenWidth / 4;

		localStage.setWidth(width);
		localStage.setHeight(height);

		localStage.setMinWidth(120);
		localStage.setMinHeight(120);

		localStage.setResizable(false);

		localStage.setMaxWidth(screenWidth);
		localStage.setMaxHeight(screenHeight);

		localStage.setScene(StageEditor.scene);
		localStage.getIcons().add(App.AGC_ICON_IMAGE);
		localStage.setTitle(App.getWindowTitleString("stageEditor"));

		localStage.widthProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			StageEditor.onListViewsWiden(p_newValue.doubleValue(), p_oldValue.doubleValue());
		});
		localStage.heightProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();
			StageEditor.onListViewsHeighten(side - (side / 12));
		});
	}

	private static void initRootPane() {
		final var localPaneRoot = StageEditor.paneRoot;

		localPaneRoot.setStyle("-fx-background-color: rgb(0, 0, 0);"); // NOSONAR! Dis CSS!
		localPaneRoot.setOnKeyPressed(p_event -> {
			final var key = p_event.getCode();

			final boolean alt = p_event.isAltDown();
			final boolean meta = p_event.isMetaDown();
			final boolean shift = p_event.isShiftDown();
			final boolean ctrl = p_event.isControlDown();

			final boolean noMods = !(ctrl || shift || alt || meta);
			final boolean onlyCtrl = ctrl && !(alt || meta || shift);
			final boolean onlyShiftCtrl = ctrl && shift && !(alt || meta); // If NO keys are active, nothing! (`0` px!)

			final var model = StageEditor.listViewProfiles.getSelectionModel();
			final var selections = model.getSelectedItems();

			switch (key) {

				case ESCAPE -> {
					StageEditor.close();
				}

				case INSERT -> {
					StageEditor.onOptionSelection(OptionsEditor.ADD);
				}
				case DELETE -> {
					StageEditor.onOptionSelection(OptionsEditor.REMOVE);
				}

				default -> {
					// No defaults!
				}

				case F1 -> {
					if (noMods)
						DialogHelp.show();
				}
				case F -> {
					StageHome.show();
					App.smartlyPositionSecondOfStages(StageEditor.stage, StageProfiles.stage);
				}

				case W -> {
					if (onlyCtrl)
						StageEditor.close();
				}

			}
		});
	}

	private static void initOptionsList() {
		final var localListView = StageEditor.listViewOptions;
		final var localLabelOptionsList = StageEditor.labelListOptions;

		localListView.requestFocus();
		localListView.getItems().addAll(OptionsEditor.ORDER_UI);
		localListView.setStyle("-fx-background-color: rgb(0, 0, 0);");

		localLabelOptionsList.setStyle("-fx-text-fill: gray;"); // NOSONAR! CSS!...

		localListView.setOnKeyPressed(p_event -> {
			final var key = p_event.getCode();

			final boolean alt = p_event.isAltDown();
			final boolean meta = p_event.isMetaDown();
			final boolean shift = p_event.isShiftDown();
			final boolean ctrl = p_event.isControlDown();

			final boolean onlyCtrl = ctrl && !(shift && alt || meta);
			final boolean onlyShift = shift && !(ctrl || alt || meta);
			final boolean onlyShiftCtrl = ctrl && shift && !(alt || meta);

			final var clientSelections = StageEditor.listViewProfiles.getSelectionModel();
			final var optionSelections = StageEditor.listViewOptions.getSelectionModel();

			final var selectedItems = clientSelections.getSelectedItems();
			final var selectedOption = optionSelections.getSelectedItem();

			switch (key) {

				case ENTER, SPACE -> StageEditor.onOptionSelection(selectedOption);

				case ESCAPE -> {
					StageEditor.close();
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
						StageEditor.close();
				}

			}
		});

		localListView.setCellFactory(p_listView -> {
			final var toRet = new ListCell<OptionsEditor>() {

				@Override
				protected void updateItem(final OptionsEditor p_option, final boolean p_isEmpty) {
					super.updateItem(p_option, p_isEmpty);

					if (super.isFocused() && localListView.isFocused())
						super.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: rgb(255, 255, 255);");
					else
						super.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: #808080;");

					if (p_option != null /* && !p_isEmpty */) {
						super.setOnMouseClicked(p_event -> {
							final var clientSelections = StageEditor.listViewProfiles.getSelectionModel();
							final var optionSelections = StageEditor.listViewOptions.getSelectionModel();

							final var selectedItems = clientSelections.getSelectedItems();
							final var selectedOption = optionSelections.getSelectedItem();

							// if (p_event.getPickResult().getIntersectedNode() != this)
							// return;

							switch (p_event.getButton()) {

								case PRIMARY -> StageEditor.onOptionSelection(selectedOption);

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

		localListView.focusedProperty().addListener((p_property, p_oldValue, p_newValue) -> {
			final boolean inFocus = p_newValue; // JavaFX ain't settin' it `null`!...
			localLabelOptionsList.setStyle(inFocus

					? "-fx-text-fill: rgb(255, 255, 255);"
					: "-fx-text-fill: gray;"

			);
		});
	}

	@SuppressWarnings("unchecked")
	private static void initProfilesList() {
		final var localLabelProfilesList = StageEditor.labelListProfiles;
		final var localListView = StageEditor.listViewProfiles;

		localLabelProfilesList.setPrefWidth(240);
		localLabelProfilesList.setStyle("-fx-text-fill: gray;"); // NOSONAR! Can't! It's CSS!
		localListView.setStyle("-fx-background-color: rgb(0, 0, 0);");

		// Used to track dragging:
		final var startId = new AtomicInteger(-1); // Also used for drag **status** - not just the first index!
		final var startCell = new AtomicReference<ListCell<Profile>>();

		final var localListViewSelectionModel = localListView.getSelectionModel();

		localListViewSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);

		final var localListViewSelections = localListViewSelectionModel.getSelectedItems();

		localListView.setStyle("-fx-background-color: rgb(0, 0, 0);");
		localListView.setOnKeyPressed(p_event -> {
			final var key = p_event.getCode();

			final boolean alt = p_event.isAltDown();
			final boolean meta = p_event.isMetaDown();
			final boolean shift = p_event.isShiftDown();
			final boolean ctrl = p_event.isControlDown();

			final boolean onlyCtrl = ctrl && !(shift && alt || meta);
			final boolean onlyShiftCtrl = ctrl && shift && !(alt || meta);

			switch (key) {

				case ESCAPE -> {
					StageEditor.close();
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
						StageEditor.close();
				}

			}

			// System.out.println("Key pressed with list-view in focus!");

			final boolean isListEmpty = localListView.getItems().isEmpty();

			if (!isListEmpty) {
				switch (key) {

					default -> {
						// No defaults!
					}

					case DELETE -> {
						System.out.println("Exporting profile...");
					}

				}
				return;
			}

			final var localListViewOptions = StageEditor.listViewOptions;
			final var model = localListViewOptions.getSelectionModel();

			// This one matters when the profiles list is empty:
			switch (key) {

				case END, PAGE_DOWN -> {
					final var items = localListViewOptions.getItems();
					// final var selections = model.getSelectedItems();
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
			final var toRet = new ListCell<Profile>() {

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
				protected void updateItem(final Profile p_profile, final boolean p_isEmpty) {
					super.updateItem(p_profile, p_isEmpty);

					if (p_isEmpty)
						localListViewSelectionModel.clearSelection();

					if (super.isSelected())
						super.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: rgb(255, 255, 255);");
					else
						super.setStyle("-fx-background-color: rgb(0, 0, 0); -fx-text-fill: #808080;");

					// This CLEARS text when it goes away!
					if (p_isEmpty || p_profile == null) { // Frequent condition first.
						super.setCursor(Cursor.DEFAULT);
						super.setText(null);
						return;
					}

					super.setCursor(Cursor.CROSSHAIR);
					super.setText(p_profile.getName());
				}

			};

			toRet.setOnMousePressed(p_event -> {
				final var myId = toRet.getIndex();
				final var myText = toRet.getText();
				final var myProfile = toRet.getItem();

				startCell.set(toRet);
				startId.set(myId);

				// try {

				// FIXME: If newer JavaFX fixes this, use next line (genuine solution):
				localListViewSelections.remove(myProfile); // Surprisingly fully reliable!
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
			localLabelProfilesList.setStyle(inFocus

					? "-fx-text-fill: rgb(255, 255, 255);"
					: "-fx-text-fill: gray;"

			);
		});
	}

	private static void initSeparatorButton() {
		final var localLabelProfiles = StageEditor.labelListProfiles;
		final var localButtonSeparator = StageEditor.buttonSeparator;
		final var localListViewProfiles = StageEditor.listViewProfiles;

		localButtonSeparator.setFocusTraversable(false);
		localButtonSeparator.setCursor(Cursor.OPEN_HAND);
		localButtonSeparator.setPrefHeight(App.PRIMARY_SCREEN_HEIGHT);
		localButtonSeparator.setStyle("-fx-background-color: rgb(50, 50, 50);");

		final var isDragging = new AtomicBoolean();

		final EventHandler<MouseEvent> localCbckMouseDrag = p_event -> {
			if (!isDragging.get())
				return;

			final double mouseX = p_event.getSceneX();

			localLabelProfiles.setPrefWidth(mouseX);
			localListViewProfiles.setPrefWidth(mouseX);
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
