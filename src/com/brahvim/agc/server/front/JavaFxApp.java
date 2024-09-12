package com.brahvim.agc.server.front;

import java.util.ArrayList;

import com.brahvim.agc.server.App;
import com.brahvim.agc.server.ExitCode;
import com.brahvim.agc.server.back.Backend;
import com.brahvim.agc.server.back.EventAwaitOneClient;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public final class JavaFxApp extends Application {

	// region Fields.
	public static final Rectangle2D PRIMARY_SCREEN_RECT = Screen.getPrimary().getBounds();
	public static final double PRIMARY_SCREEN_WIDTH = JavaFxApp.PRIMARY_SCREEN_RECT.getWidth();
	public static final double PRIMARY_SCREEN_HEIGHT = JavaFxApp.PRIMARY_SCREEN_RECT.getHeight();

	private static final EventHandler<KeyEvent> cbckKeyPressedForUndo = JavaFxApp::cbckKeyPressedForUndo;
	private static final ChangeListener<Number> cbckChangeStageWidthEnsureCenter = JavaFxApp::cbckChangeForStageWidth;

	private static final Font fontForButtons = new Font(15);

	// NOSONAR, these are to be used *anywhere* in this class!

	private static HBox row1 = null; // NOSONAR!
	private static VBox col1 = null; // NOSONAR!
	private static Stage stage = null; // NOSONAR!
	private static Scene scene = null; // NOSONAR!
	private static Pane paneRoot = null; // NOSONAR!
	private static Menu menuClick = null; // NOSONAR!
	private static MenuBar menuBar = null; // NOSONAR!
	private static Button[] buttonsAll = null; // NOSONAR!
	private static Button buttonDecClientCount = null; // NOSONAR!
	private static Button buttonLetClientConnect = null; // NOSONAR!
	private static Button buttonShowClientDisplay = null; // NOSONAR!
	private static ListView<String> listViewClientList = null; // NOSONAR!
	// endregion

	@Override
	public void stop() {
		App.exit(ExitCode.OKAY);
	}

	@Override
	public void start(final Stage p_stage) {
		final var localStage = JavaFxApp.stage = p_stage;

		final var localButtonShowClientDisplay
		/* */ = JavaFxApp.buttonShowClientDisplay
		/* */ /* */ = new Button("Open controls for selected");

		final var localButtonDecClientCount
		/* */ = JavaFxApp.buttonDecClientCount
		/* */ /* */ = new Button("Stop awaiting clients");

		final var localButtonLetClientConnect
		/* */ = JavaFxApp.buttonLetClientConnect
		/* */ /* */ = new Button("Add client");

		final var localListViewForClients = JavaFxApp.listViewClientList = new ListView<>();
		final var localCol1 = JavaFxApp.col1 = new VBox();

		final var localRow1 = JavaFxApp.row1 = new HBox(localListViewForClients, localCol1);
		final var localPaneRoot = JavaFxApp.paneRoot = new VBox(localRow1);
		final var localMenuClick = JavaFxApp.menuClick = new Menu("Click!");
		final var localMenuBar = JavaFxApp.menuBar = new MenuBar(localMenuClick);

		localListViewForClients.getItems().addAll(this.createFakeData()); // Can't pass to constructor. Weird.

		final var localScene = JavaFxApp.scene = new Scene(localPaneRoot);
		localStage.setScene(localScene);

		this.initStage();
		this.initRootPane();
		this.initClientList();
		this.initControlDisplayButton();
		this.initDecClientCountButton();
		this.initLetClientConnectButton();
		this.initButtons(

				localButtonShowClientDisplay,
				localButtonDecClientCount,
				localButtonLetClientConnect

		);

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

		JavaFxApp.stage.setMinHeight(height);
		JavaFxApp.stage.setMinWidth(width);
		JavaFxApp.stage.setHeight(height);
		JavaFxApp.stage.setWidth(width);

		JavaFxApp.stage.widthProperty().addListener(JavaFxApp.cbckChangeStageWidthEnsureCenter);

		JavaFxApp.stage.widthProperty().addListener((p_observable, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();

			JavaFxApp.listViewClientList.setPrefWidth(side - (side / 1.5));

			for (int i = 0; i < JavaFxApp.buttonsAll.length; ++i) {
				final var b = JavaFxApp.buttonsAll[i];
				b.setTranslateX(side / 12 + ((i - 1) * (JavaFxApp.stage.getWidth() / 16)));
			}
		});

		JavaFxApp.stage.heightProperty().addListener((p_observable, p_oldValue, p_newValue) -> {
			final double side = p_newValue.doubleValue();

			JavaFxApp.listViewClientList.setPrefHeight(side - (side / 6));

			for (int i = 0; i < JavaFxApp.buttonsAll.length; ++i) {
				final var b = JavaFxApp.buttonsAll[i];
				b.setTranslateY(side / 18 + ((i - 1) * (JavaFxApp.stage.getHeight() / 24)));
			}
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

	private void initClientList() {
		JavaFxApp.listViewClientList.setOnKeyPressed(p_keyEvent -> {
			// System.out.println("Key pressed with list-view in focus!");
			switch (p_keyEvent.getCode()) {

				case DELETE ->
					JavaFxApp.listViewClientList.getItems()
							.removeAll(JavaFxApp.listViewClientList.getSelectionModel().getSelectedItems());

				default -> {
					//
				}

			}
		});

		JavaFxApp.listViewClientList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		JavaFxApp.listViewClientList.setCellFactory(p_toGenFor -> new ListCell<String>() {

			@Override
			protected void updateItem(final String p_item, final boolean p_isEmpty) {
				super.updateItem(p_item, p_isEmpty);
				super.setText(p_item);
				super.getHeight();

				super.setOnKeyPressed(p_keyEvent -> {
					System.out.println("Key pressed with list cell focused.");
					if (p_keyEvent.getCode() == KeyCode.DELETE) {
						super.getListView().getItems().remove(p_item);
					}
				});

			}
		});

		// clientsList.getSelectionModel().selectedItemProperty().addListener(
		// (p_observable, p_oldValue, p_newValue) ->
		// System.out.printf("Item `%s` selected!%n", p_newValue));
	}

	private void initControlDisplayButton() {
		final Button button = JavaFxApp.buttonShowClientDisplay;

		button.setOnAction(p_event -> {
			final var selections = JavaFxApp.listViewClientList.getSelectionModel().getSelectedItems();

			// for (final String s : selections) { }

			System.out.printf(

					"Controls button pressed for clients \"%s\".%n",
					selections.toString()

			);
		});
	}

	private void initDecClientCountButton() {
		JavaFxApp.buttonDecClientCount.setOnAction(p_event -> {
			Backend.INT_CLIENTS_LEFT.set(0);
			System.out.println("Welcome socket asked to stop.");
		});
	}

	private void initLetClientConnectButton() {
		final Button button = JavaFxApp.buttonLetClientConnect;

		button.setOnAction(p_event -> {
			Backend.EDT.publish(EventAwaitOneClient.create());
		});
	}

	private void initButtons(final Button... p_buttons) {
		JavaFxApp.buttonsAll = p_buttons;
		JavaFxApp.col1.getChildren().addAll(p_buttons);

		for (final Button b : JavaFxApp.buttonsAll)
			b.setFont(JavaFxApp.fontForButtons);
	}
	// endregion

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
